package malbeth.javautils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Blacklist {
    private Set<BlacklistAddress> ipAddresses = new HashSet<BlacklistAddress>();
    private List<BlacklistRule> rules = new LinkedList<BlacklistRule>();

    public static class BlacklistAddress implements Comparable<BlacklistAddress> {
        public short[] parts = new short[4];

        private static final Pattern addressSplitter = Pattern.compile("(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)");

        public BlacklistAddress(short part1, short part2, short part3, short part4) {
            parts[0] = part1;
            parts[1] = part2;
            parts[2] = part3;
            parts[3] = part4;
        }

        @Override
        public int compareTo(BlacklistAddress address) {
            if (address != null) {
                if (parts[0] < address.parts[0])
                    return -1;
                if (parts[0] > address.parts[0])
                    return 1;
                if (parts[1] < address.parts[1])
                    return -1;
                if (parts[1] > address.parts[1])
                    return 1;
                if (parts[2] < address.parts[2])
                    return -1;
                if (parts[2] > address.parts[2])
                    return 1;
                if (parts[3] < address.parts[3])
                    return -1;
                if (parts[3] > address.parts[3])
                    return 1;
                else
                    return 0;
            }

            return 1;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof BlacklistAddress)
                return (this.compareTo((BlacklistAddress) obj) == 0);
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 31 * hash + parts[0];
            hash = 31 * hash + parts[1];
            hash = 31 * hash + parts[2];
            hash = 31 * hash + parts[3];
            return hash;
        }

        public static BlacklistAddress parse(String value) {
            if (value != null) {
                Matcher from_match = addressSplitter.matcher(value);
                if (from_match.find())
                    return new BlacklistAddress(Short.parseShort(from_match.group(1)), Short.parseShort(from_match.group(2)), Short.parseShort(from_match.group(3)), Short.parseShort(from_match.group(4)));
            }

            return null;
        }

        @Override
        public String toString() {
            return parts[0] + "." + parts[1] + "." + parts[2] + "." + parts[3];
        }
    }

    public static class BlacklistRule {
        public BlacklistAddress fromAddress;
        public BlacklistAddress toAddress;

        public String source;

        public boolean matches(String value) {
            if (value != null) {
                BlacklistAddress address = BlacklistAddress.parse(value);

                if (fromAddress != null) {
                    if (fromAddress.compareTo(address) == 0)
                        return true;
                    else if (toAddress != null) {
                        if (toAddress.compareTo(address) == 0)
                            return true;
                        else if (fromAddress.compareTo(address) < 0 && toAddress.compareTo(address) > 0)
                            return true;
                    }
                }
            }

            return false;
        }
    }

    public boolean add(String value) {
        if (value != null && value.length() > 0) {
            int index = value.indexOf('-');
            if (index < 0) {
                ipAddresses.add(BlacklistAddress.parse(value));

                return true;
            } else if (index > 0) {
                BlacklistRule rule = new BlacklistRule();
                rule.fromAddress = BlacklistAddress.parse(value.substring(0, index));
                rule.toAddress = BlacklistAddress.parse(value.substring(index + 1));
                rule.source = value;
                rules.add(rule);

                return true;
            }
        }

        return false;
    }

    public boolean contains(String value) {
        if (ipAddresses.contains(BlacklistAddress.parse(value)))
            return true;
        else {
            for (ListIterator<BlacklistRule> it = rules.listIterator(); it.hasNext(); ) {
                if (it.next().matches(value))
                    return true;
            }
        }

        return false;
    }

    public boolean remove(String value) {
        if (ipAddresses.remove(BlacklistAddress.parse(value)))
            return true;
        else {
            for (ListIterator<BlacklistRule> it = rules.listIterator(); it.hasNext(); ) {
                if (it.next().source == value) {
                    it.remove();

                    return true;
                }
            }
        }

        return false;
    }

}
