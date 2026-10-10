package redis;

public class CommandHandler {

    private final RedisDatabase database;

    public CommandHandler(RedisDatabase database) {
        this.database = database;
    }

    public String execute(String command) {

        if (command == null || command.trim().isEmpty()) {
            return "ERROR: Empty command";
        }

        String[] parts = command.trim().split("\\s+", 3);
        String operation = parts[0].toUpperCase();

        try {

            // BASIC
            if (operation.equals("PING")) {
                return "PONG";
            }

            if (operation.equals("QUIT")) {
                return "Goodbye!";
            }

            // STRING COMMANDS
            if (operation.equals("SET")) {
                if (parts.length < 3) return "ERROR: Usage SET key value";
                database.set(parts[1], parts[2]);
                return "OK";
            }

            if (operation.equals("GET")) {
                if (parts.length < 2) return "ERROR: Usage GET key";
                String value = database.get(parts[1]);
                return value == null ? "(nil)" : value;
            }

            if (operation.equals("DEL")) {
                if (parts.length < 2) return "ERROR: Usage DEL key";
                database.delete(parts[1]);
                return "1";
            }

            if (operation.equals("EXISTS")) {
                if (parts.length < 2) return "ERROR: Usage EXISTS key";
                return database.exists(parts[1]) ? "1" : "0";
            }

            if (operation.equals("INCR")) {
                if (parts.length < 2) return "ERROR: Usage INCR key";
                return String.valueOf(database.increment(parts[1]));
            }

            if (operation.equals("DECR")) {
                if (parts.length < 2) return "ERROR: Usage DECR key";
                return String.valueOf(database.decrement(parts[1]));
            }

            if (operation.equals("APPEND")) {
                if (parts.length < 3) return "ERROR: Usage APPEND key value";
                return String.valueOf(database.append(parts[1], parts[2]));
            }

            if (operation.equals("STRLEN")) {
                if (parts.length < 2) return "ERROR: Usage STRLEN key";
                return String.valueOf(database.strlen(parts[1]));
            }

            if (operation.equals("MSET")) {
                if (parts.length < 3) return "ERROR: Usage MSET key value [key value ...]";
                String[] values = (parts[1] + " " + parts[2]).split("\\s+");
                return database.mset(values);
            }

            if (operation.equals("MGET")) {
                if (parts.length < 2) return "ERROR: Usage MGET key [key ...]";
                String[] keys = (parts[1] + (parts.length == 3 ? " " + parts[2] : "")).split("\\s+");
                return database.mget(keys);
            }

            // DATABASE COMMANDS
            if (operation.equals("KEYS")) {
                return String.join(" ", database.keys());
            }

            if (operation.equals("DBSIZE")) {
                return String.valueOf(database.dbsize());
            }

            if (operation.equals("FLUSHDB")) {
                database.flush();
                return "OK";
            }

            if (operation.equals("TYPE")) {
                if (parts.length < 2) return "ERROR: Usage TYPE key";
                return database.type(parts[1]);
            }

            // EXPIRATION COMMANDS
            if (operation.equals("EXPIRE")) {
                if (parts.length < 3) return "ERROR: Usage EXPIRE key seconds";
                return database.expire(parts[1], Integer.parseInt(parts[2])) ? "1" : "0";
            }

            if (operation.equals("TTL")) {
                if (parts.length < 2) return "ERROR: Usage TTL key";
                return String.valueOf(database.ttl(parts[1]));
            }

            // LIST COMMANDS
            if (operation.equals("LPUSH")) {
                if (parts.length < 3) return "ERROR: Usage LPUSH key value";
                return String.valueOf(database.lpush(parts[1], parts[2]));
            }

            if (operation.equals("RPUSH")) {
                if (parts.length < 3) return "ERROR: Usage RPUSH key value";
                return String.valueOf(database.rpush(parts[1], parts[2]));
            }

            if (operation.equals("LRANGE")) {
                if (parts.length < 3) return "ERROR: Usage LRANGE key start end";
                String[] range = parts[2].split("\\s+");
                if (range.length < 2) return "ERROR: Usage LRANGE key start end";
                return database.lrange(parts[1],
                        Integer.parseInt(range[0]),
                        Integer.parseInt(range[1]));
            }

            if (operation.equals("LPOP")) {
                if (parts.length < 2) return "ERROR: Usage LPOP key";
                return database.lpop(parts[1]);
            }

            if (operation.equals("RPOP")) {
                if (parts.length < 2) return "ERROR: Usage RPOP key";
                return database.rpop(parts[1]);
            }

            if (operation.equals("LLEN")) {
                if (parts.length < 2) return "ERROR: Usage LLEN key";
                return String.valueOf(database.llen(parts[1]));
            }

            if (operation.equals("LINDEX")) {
                if (parts.length < 3) return "ERROR: Usage LINDEX key index";
                return database.lindex(parts[1], Integer.parseInt(parts[2]));
            }

            if (operation.equals("LSET")) {
                if (parts.length < 3) return "ERROR: Usage LSET key index value";
                String[] values = parts[2].split("\\s+", 2);
                if (values.length < 2) return "ERROR: Usage LSET key index value";
                return database.lset(parts[1], Integer.parseInt(values[0]), values[1]);
            }

            if (operation.equals("LTRIM")) {
                if (parts.length < 3) return "ERROR: Usage LTRIM key start end";
                String[] range = parts[2].split("\\s+");
                if (range.length < 2) return "ERROR: Usage LTRIM key start end";
                return database.ltrim(parts[1],
                        Integer.parseInt(range[0]),
                        Integer.parseInt(range[1]));
            }

            if (operation.equals("LREM")) {
                if (parts.length < 3) return "ERROR: Usage LREM key count value";
                String[] values = parts[2].split("\\s+", 2);
                if (values.length < 2) return "ERROR: Usage LREM key count value";
                return String.valueOf(database.lrem(
                        parts[1], Integer.parseInt(values[0]), values[1]));
            }

            if (operation.equals("LINSERT")) {
                if (parts.length < 3) return "ERROR: Usage LINSERT key BEFORE|AFTER pivot value";
                String[] values = parts[2].split("\\s+", 3);
                if (values.length < 3) return "ERROR: Usage LINSERT key BEFORE|AFTER pivot value";
                return String.valueOf(database.linsert(
                        parts[1], values[0], values[1], values[2]));
            }

            // SET COMMANDS
            if (operation.equals("SADD")) {
                if (parts.length < 3) return "ERROR: Usage SADD key member";
                return String.valueOf(database.sadd(parts[1], parts[2]));
            }

            if (operation.equals("SREM")) {
                if (parts.length < 3) return "ERROR: Usage SREM key member";
                return String.valueOf(database.srem(parts[1], parts[2]));
            }

            if (operation.equals("SMEMBERS")) {
                if (parts.length < 2) return "ERROR: Usage SMEMBERS key";
                return database.smembers(parts[1]);
            }

            if (operation.equals("SISMEMBER")) {
                if (parts.length < 3) return "ERROR: Usage SISMEMBER key member";
                return String.valueOf(database.sismember(parts[1], parts[2]));
            }

            if (operation.equals("SCARD")) {
                if (parts.length < 2) return "ERROR: Usage SCARD key";
                return String.valueOf(database.scard(parts[1]));
            }

            if (operation.equals("SPOP")) {
                if (parts.length < 2) return "ERROR: Usage SPOP key";
                return database.spop(parts[1]);
            }

            if (operation.equals("SRANDMEMBER")) {
                if (parts.length < 2) return "ERROR: Usage SRANDMEMBER key";
                return database.srandmember(parts[1]);
            }

            if (operation.equals("SUNION") || operation.equals("SINTER")
                    || operation.equals("SDIFF")) {
                if (parts.length < 3) return "ERROR: Usage " + operation + " key1 key2";
                String[] keys = parts[2].split("\\s+");
                if (operation.equals("SUNION")) return database.sunion(parts[1], keys[0]);
                if (operation.equals("SINTER")) return database.sinter(parts[1], keys[0]);
                return database.sdiff(parts[1], keys[0]);
            }

            if (operation.equals("SMOVE")) {
                if (parts.length < 3) return "ERROR: Usage SMOVE source destination member";
                String[] values = parts[2].split("\\s+", 2);
                if (values.length < 2) return "ERROR: Usage SMOVE source destination member";
                return String.valueOf(database.smove(parts[1], values[0], values[1]));
            }

            if (operation.equals("SUNIONSTORE")
                    || operation.equals("SINTERSTORE")
                    || operation.equals("SDIFFSTORE")) {
                if (parts.length < 3) return "ERROR: Usage " + operation + " destination key1 key2";
                String[] keys = parts[2].split("\\s+");
                if (keys.length < 2) return "ERROR: Usage " + operation + " destination key1 key2";
                if (operation.equals("SUNIONSTORE")) return String.valueOf(database.sunionstore(parts[1], keys[0], keys[1]));
                if (operation.equals("SINTERSTORE")) return String.valueOf(database.sinterstore(parts[1], keys[0], keys[1]));
                return String.valueOf(database.sdiffstore(parts[1], keys[0], keys[1]));
            }

            // HASH COMMANDS
            if (operation.equals("HSET")) {
                if (parts.length < 3) return "ERROR: Usage HSET key field value";
                String[] values = parts[2].split("\\s+", 2);
                if (values.length < 2) return "ERROR: Usage HSET key field value";
                return String.valueOf(database.hset(parts[1], values[0], values[1]));
            }

            if (operation.equals("HGET")) {
                if (parts.length < 3) return "ERROR: Usage HGET key field";
                return database.hget(parts[1], parts[2]);
            }

            if (operation.equals("HDEL")) {
                if (parts.length < 3) return "ERROR: Usage HDEL key field";
                return String.valueOf(database.hdel(parts[1], parts[2]));
            }

            if (operation.equals("HEXISTS")) {
                if (parts.length < 3) return "ERROR: Usage HEXISTS key field";
                return String.valueOf(database.hexists(parts[1], parts[2]));
            }

            if (operation.equals("HGETALL")) {
                if (parts.length < 2) return "ERROR: Usage HGETALL key";
                return database.hgetall(parts[1]);
            }

            if (operation.equals("HLEN")) {
                if (parts.length < 2) return "ERROR: Usage HLEN key";
                return String.valueOf(database.hlen(parts[1]));
            }

            // SORTED SET COMMANDS
            if (operation.equals("ZADD")) {
                if (parts.length < 3) return "ERROR: Usage ZADD key score member";
                String[] values = parts[2].split("\\s+", 2);
                if (values.length < 2) return "ERROR: Usage ZADD key score member";
                return String.valueOf(database.zadd(
                        parts[1], Double.parseDouble(values[0]), values[1]));
            }

            if (operation.equals("ZSCORE")) {
                if (parts.length < 3) return "ERROR: Usage ZSCORE key member";
                return database.zscore(parts[1], parts[2]);
            }

            if (operation.equals("ZREM")) {
                if (parts.length < 3) return "ERROR: Usage ZREM key member";
                return String.valueOf(database.zrem(parts[1], parts[2]));
            }

            if (operation.equals("ZCARD")) {
                if (parts.length < 2) return "ERROR: Usage ZCARD key";
                return String.valueOf(database.zcard(parts[1]));
            }

            if (operation.equals("ZRANGE")) {
                if (parts.length < 3) return "ERROR: Usage ZRANGE key start end";
                String[] range = parts[2].split("\\s+");
                if (range.length < 2) return "ERROR: Usage ZRANGE key start end";
                return database.zrange(parts[1],
                        Integer.parseInt(range[0]),
                        Integer.parseInt(range[1]));
            }

            return "ERROR: Unknown command";

        } catch (NumberFormatException e) {
            return "ERROR: Invalid number";
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
}