package redis;

import java.util.*;

public class RedisDatabase {

    private final Map<String, String> database = new HashMap<>();
    private final Map<String, Long> expiryTimes = new HashMap<>();
    private final Map<String, List<String>> lists = new HashMap<>();
    private final Map<String, Set<String>> sets = new HashMap<>();
    private final Map<String, Map<String, String>> hashes = new HashMap<>();
    private final Map<String, Map<String, Double>> sortedSets = new HashMap<>();

    // ==================== STRING METHODS ====================

    public void set(String key, String value) {
        checkExpiry(key);
        database.put(key, value);
        lists.remove(key);
        sets.remove(key);
        hashes.remove(key);
        sortedSets.remove(key);
    }

    public String get(String key) {
        checkExpiry(key);
        return database.get(key);
    }

    public void delete(String key) {
        database.remove(key);
        lists.remove(key);
        sets.remove(key);
        hashes.remove(key);
        sortedSets.remove(key);
        expiryTimes.remove(key);
    }

    public boolean exists(String key) {
        checkExpiry(key);

        return database.containsKey(key)
                || lists.containsKey(key)
                || sets.containsKey(key)
                || hashes.containsKey(key)
                || sortedSets.containsKey(key);
    }

    public int increment(String key) {
        checkExpiry(key);

        String current = database.get(key);
        int value = current == null ? 1 : Integer.parseInt(current) + 1;

        database.put(key, String.valueOf(value));
        return value;
    }

    public int decrement(String key) {
        checkExpiry(key);

        String current = database.get(key);
        int value = current == null ? -1 : Integer.parseInt(current) - 1;

        database.put(key, String.valueOf(value));
        return value;
    }

    public int append(String key, String value) {
        checkExpiry(key);

        String oldValue = database.getOrDefault(key, "");
        String newValue = oldValue + value;

        database.put(key, newValue);
        return newValue.length();
    }

    public int strlen(String key) {
        String value = get(key);
        return value == null ? 0 : value.length();
    }

    public String mset(String[] values) {
        if (values.length < 2 || values.length % 2 != 0) {
            return "ERROR: MSET requires key-value pairs";
        }

        for (int i = 0; i < values.length; i += 2) {
            set(values[i], values[i + 1]);
        }

        return "OK";
    }

    public String mget(String[] keys) {
        List<String> result = new ArrayList<>();

        for (String key : keys) {
            String value = get(key);
            result.add(value == null ? "(nil)" : value);
        }

        return String.join(" ", result);
    }

    // ==================== DATABASE METHODS ====================

    public Set<String> keys() {
        Set<String> allKeys = new HashSet<>();

        allKeys.addAll(database.keySet());
        allKeys.addAll(lists.keySet());
        allKeys.addAll(sets.keySet());
        allKeys.addAll(hashes.keySet());
        allKeys.addAll(sortedSets.keySet());

        for (String key : new HashSet<>(allKeys)) {
            checkExpiry(key);
        }

        allKeys.clear();
        allKeys.addAll(database.keySet());
        allKeys.addAll(lists.keySet());
        allKeys.addAll(sets.keySet());
        allKeys.addAll(hashes.keySet());
        allKeys.addAll(sortedSets.keySet());

        return allKeys;
    }

    public int dbsize() {
        return keys().size();
    }

    public void flush() {
        database.clear();
        expiryTimes.clear();
        lists.clear();
        sets.clear();
        hashes.clear();
        sortedSets.clear();
    }

    public String type(String key) {
        checkExpiry(key);

        if (database.containsKey(key)) return "string";
        if (lists.containsKey(key)) return "list";
        if (sets.containsKey(key)) return "set";
        if (hashes.containsKey(key)) return "hash";
        if (sortedSets.containsKey(key)) return "zset";

        return "none";
    }

    // ==================== EXPIRATION METHODS ====================

    public boolean expire(String key, int seconds) {
        if (!exists(key)) {
            return false;
        }

        if (seconds <= 0) {
            delete(key);
            return true;
        }

        expiryTimes.put(
                key,
                System.currentTimeMillis() + seconds * 1000L
        );

        return true;
    }

    public long ttl(String key) {
        checkExpiry(key);

        if (!exists(key)) return -2;
        if (!expiryTimes.containsKey(key)) return -1;

        long remaining =
                expiryTimes.get(key) - System.currentTimeMillis();

        if (remaining <= 0) {
            delete(key);
            return -2;
        }

        return (remaining + 999) / 1000;
    }

    private void checkExpiry(String key) {
        Long expiry = expiryTimes.get(key);

        if (expiry != null &&
                System.currentTimeMillis() >= expiry) {
            delete(key);
        }
    }

    // ==================== LIST METHODS ====================

    public int lpush(String key, String value) {
        checkExpiry(key);

        database.remove(key);
        sets.remove(key);
        hashes.remove(key);
        sortedSets.remove(key);

        lists.computeIfAbsent(key, k -> new ArrayList<>())
                .add(0, value);

        return lists.get(key).size();
    }

    public int rpush(String key, String value) {
        checkExpiry(key);

        database.remove(key);
        sets.remove(key);
        hashes.remove(key);
        sortedSets.remove(key);

        lists.computeIfAbsent(key, k -> new ArrayList<>())
                .add(value);

        return lists.get(key).size();
    }

    public String lrange(String key, int start, int end) {
        checkExpiry(key);

        List<String> list = lists.get(key);
        if (list == null) return "(nil)";

        int size = list.size();

        if (start < 0) start += size;
        if (end < 0) end += size;

        start = Math.max(start, 0);
        end = Math.min(end, size - 1);

        if (start > end || start >= size) return "";

        return String.join(" ", list.subList(start, end + 1));
    }

    public String lpop(String key) {
        checkExpiry(key);

        List<String> list = lists.get(key);
        if (list == null || list.isEmpty()) return "(nil)";

        String value = list.remove(0);

        if (list.isEmpty()) lists.remove(key);

        return value;
    }

    public String rpop(String key) {
        checkExpiry(key);

        List<String> list = lists.get(key);
        if (list == null || list.isEmpty()) return "(nil)";

        String value = list.remove(list.size() - 1);

        if (list.isEmpty()) lists.remove(key);

        return value;
    }

    public int llen(String key) {
        checkExpiry(key);

        List<String> list = lists.get(key);
        return list == null ? 0 : list.size();
    }

    public String lindex(String key, int index) {
        checkExpiry(key);

        List<String> list = lists.get(key);
        if (list == null) return "(nil)";

        if (index < 0) index += list.size();

        if (index < 0 || index >= list.size()) {
            return "(nil)";
        }

        return list.get(index);
    }

    public String lset(String key, int index, String value) {
        checkExpiry(key);

        List<String> list = lists.get(key);
        if (list == null) return "ERROR: No such key";

        if (index < 0) index += list.size();

        if (index < 0 || index >= list.size()) {
            return "ERROR: Index out of range";
        }

        list.set(index, value);
        return "OK";
    }

    public String ltrim(String key, int start, int end) {
        checkExpiry(key);

        List<String> list = lists.get(key);
        if (list == null) return "OK";

        int size = list.size();

        if (start < 0) start += size;
        if (end < 0) end += size;

        start = Math.max(start, 0);
        end = Math.min(end, size - 1);

        if (start > end || start >= size) {
            lists.remove(key);
            return "OK";
        }

        lists.put(
                key,
                new ArrayList<>(list.subList(start, end + 1))
        );

        return "OK";
    }

    public int lrem(String key, int count, String value) {
        checkExpiry(key);

        List<String> list = lists.get(key);
        if (list == null) return 0;

        int removed = 0;

        if (count >= 0) {
            Iterator<String> iterator = list.iterator();

            while (iterator.hasNext() &&
                    (count == 0 || removed < count)) {
                if (iterator.next().equals(value)) {
                    iterator.remove();
                    removed++;
                }
            }
        } else {
            ListIterator<String> iterator =
                    list.listIterator(list.size());

            while (iterator.hasPrevious() && removed < -count) {
                if (iterator.previous().equals(value)) {
                    iterator.remove();
                    removed++;
                }
            }
        }

        if (list.isEmpty()) lists.remove(key);

        return removed;
    }

    public int linsert(
            String key,
            String position,
            String pivot,
            String value
    ) {
        checkExpiry(key);

        List<String> list = lists.get(key);
        if (list == null) return 0;

        int index = list.indexOf(pivot);
        if (index == -1) return -1;

        if (position.equalsIgnoreCase("BEFORE")) {
            list.add(index, value);
        } else if (position.equalsIgnoreCase("AFTER")) {
            list.add(index + 1, value);
        } else {
            return -1;
        }

        return list.size();
    }

    // ==================== SET METHODS ====================

    public int sadd(String key, String value) {
        checkExpiry(key);

        database.remove(key);
        lists.remove(key);
        hashes.remove(key);
        sortedSets.remove(key);

        return sets.computeIfAbsent(key, k -> new HashSet<>())
                .add(value) ? 1 : 0;
    }

    public int srem(String key, String value) {
        checkExpiry(key);

        Set<String> set = sets.get(key);
        if (set == null) return 0;

        boolean removed = set.remove(value);

        if (set.isEmpty()) sets.remove(key);

        return removed ? 1 : 0;
    }

    public String smembers(String key) {
        checkExpiry(key);

        Set<String> set = sets.get(key);

        if (set == null || set.isEmpty()) {
            return "(empty set)";
        }

        return String.join(" ", set);
    }

    public int sismember(String key, String value) {
        checkExpiry(key);

        Set<String> set = sets.get(key);

        return set != null && set.contains(value) ? 1 : 0;
    }

    public int scard(String key) {
        checkExpiry(key);

        Set<String> set = sets.get(key);
        return set == null ? 0 : set.size();
    }

    public String spop(String key) {
        checkExpiry(key);

        Set<String> set = sets.get(key);
        if (set == null || set.isEmpty()) return "(nil)";

        String value = set.iterator().next();
        set.remove(value);

        if (set.isEmpty()) sets.remove(key);

        return value;
    }

    public String srandmember(String key) {
        checkExpiry(key);

        Set<String> set = sets.get(key);
        if (set == null || set.isEmpty()) return "(nil)";

        int index = new Random().nextInt(set.size());

        return new ArrayList<>(set).get(index);
    }

    public String sunion(String key1, String key2) {
        checkExpiry(key1);
        checkExpiry(key2);

        Set<String> result = new HashSet<>();

        if (sets.containsKey(key1)) result.addAll(sets.get(key1));
        if (sets.containsKey(key2)) result.addAll(sets.get(key2));

        return result.isEmpty()
                ? "(empty set)"
                : String.join(" ", result);
    }

    public String sinter(String key1, String key2) {
        checkExpiry(key1);
        checkExpiry(key2);

        Set<String> result = new HashSet<>();

        if (sets.containsKey(key1) && sets.containsKey(key2)) {
            result.addAll(sets.get(key1));
            result.retainAll(sets.get(key2));
        }

        return result.isEmpty()
                ? "(empty set)"
                : String.join(" ", result);
    }

    public String sdiff(String key1, String key2) {
        checkExpiry(key1);
        checkExpiry(key2);

        Set<String> result = new HashSet<>();

        if (sets.containsKey(key1)) {
            result.addAll(sets.get(key1));
        }

        if (sets.containsKey(key2)) {
            result.removeAll(sets.get(key2));
        }

        return result.isEmpty()
                ? "(empty set)"
                : String.join(" ", result);
    }

    public int smove(String source, String destination, String value) {
        checkExpiry(source);
        checkExpiry(destination);

        Set<String> sourceSet = sets.get(source);

        if (sourceSet == null || !sourceSet.remove(value)) {
            return 0;
        }

        sets.computeIfAbsent(destination, k -> new HashSet<>())
                .add(value);

        if (sourceSet.isEmpty()) sets.remove(source);

        return 1;
    }

    public int sunionstore(
            String destination,
            String key1,
            String key2
    ) {
        Set<String> result = new HashSet<>();

        if (sets.containsKey(key1)) result.addAll(sets.get(key1));
        if (sets.containsKey(key2)) result.addAll(sets.get(key2));

        sets.put(destination, result);

        return result.size();
    }

    public int sinterstore(
            String destination,
            String key1,
            String key2
    ) {
        Set<String> result = new HashSet<>();

        if (sets.containsKey(key1) && sets.containsKey(key2)) {
            result.addAll(sets.get(key1));
            result.retainAll(sets.get(key2));
        }

        sets.put(destination, result);

        return result.size();
    }

    public int sdiffstore(
            String destination,
            String key1,
            String key2
    ) {
        Set<String> result = new HashSet<>();

        if (sets.containsKey(key1)) result.addAll(sets.get(key1));
        if (sets.containsKey(key2)) result.removeAll(sets.get(key2));

        sets.put(destination, result);

        return result.size();
    }

    // ==================== HASH METHODS ====================

    public int hset(String key, String field, String value) {
        checkExpiry(key);

        database.remove(key);
        lists.remove(key);
        sets.remove(key);
        sortedSets.remove(key);

        Map<String, String> hash =
                hashes.computeIfAbsent(key, k -> new HashMap<>());

        boolean isNew = !hash.containsKey(field);
        hash.put(field, value);

        return isNew ? 1 : 0;
    }

    public String hget(String key, String field) {
        checkExpiry(key);

        Map<String, String> hash = hashes.get(key);

        return hash == null
                ? "(nil)"
                : hash.getOrDefault(field, "(nil)");
    }

    public int hdel(String key, String field) {
        checkExpiry(key);

        Map<String, String> hash = hashes.get(key);

        if (hash == null || hash.remove(field) == null) {
            return 0;
        }

        if (hash.isEmpty()) hashes.remove(key);

        return 1;
    }

    public int hexists(String key, String field) {
        checkExpiry(key);

        Map<String, String> hash = hashes.get(key);

        return hash != null && hash.containsKey(field) ? 1 : 0;
    }

    public String hgetall(String key) {
        checkExpiry(key);

        Map<String, String> hash = hashes.get(key);

        if (hash == null || hash.isEmpty()) {
            return "(empty hash)";
        }

        List<String> result = new ArrayList<>();

        for (Map.Entry<String, String> entry : hash.entrySet()) {
            result.add(entry.getKey());
            result.add(entry.getValue());
        }

        return String.join(" ", result);
    }

    public int hlen(String key) {
        checkExpiry(key);

        Map<String, String> hash = hashes.get(key);

        return hash == null ? 0 : hash.size();
    }

    // ==================== SORTED SET METHODS ====================

    public int zadd(String key, double score, String member) {
        checkExpiry(key);

        database.remove(key);
        lists.remove(key);
        sets.remove(key);
        hashes.remove(key);

        Map<String, Double> sorted =
                sortedSets.computeIfAbsent(key, k -> new HashMap<>());

        boolean isNew = !sorted.containsKey(member);
        sorted.put(member, score);

        return isNew ? 1 : 0;
    }

    public String zscore(String key, String member) {
        checkExpiry(key);

        Map<String, Double> sorted = sortedSets.get(key);

        if (sorted == null || !sorted.containsKey(member)) {
            return "(nil)";
        }

        return String.valueOf(sorted.get(member));
    }

    public int zrem(String key, String member) {
        checkExpiry(key);

        Map<String, Double> sorted = sortedSets.get(key);

        if (sorted == null || sorted.remove(member) == null) {
            return 0;
        }

        if (sorted.isEmpty()) sortedSets.remove(key);

        return 1;
    }

    public int zcard(String key) {
        checkExpiry(key);

        Map<String, Double> sorted = sortedSets.get(key);

        return sorted == null ? 0 : sorted.size();
    }

    public String zrange(String key, int start, int end) {
        checkExpiry(key);

        Map<String, Double> sorted = sortedSets.get(key);

        if (sorted == null || sorted.isEmpty()) {
            return "(empty zset)";
        }

        List<Map.Entry<String, Double>> entries =
                new ArrayList<>(sorted.entrySet());

        entries.sort(Map.Entry.comparingByValue());

        int size = entries.size();

        if (start < 0) start += size;
        if (end < 0) end += size;

        start = Math.max(start, 0);
        end = Math.min(end, size - 1);

        if (start > end || start >= size) return "";

        List<String> result = new ArrayList<>();

        for (int i = start; i <= end; i++) {
            result.add(entries.get(i).getKey());
        }

        return String.join(" ", result);
    }
}