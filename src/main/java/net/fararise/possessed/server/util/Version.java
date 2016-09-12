package net.fararise.possessed.server.util;

public class Version {
    private final int major;
    private final int minor;
    private final int patch;

    public Version(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public static Version parse(String version) {
        int[] parts = new int[3];
        String currentPart = "";
        int partIndex = 0;
        for (char c : version.toCharArray()) {
            if (c >= '0' && c <= '9') {
                currentPart += c;
            } else if (c == '.') {
                parts[partIndex++] = Integer.parseInt(currentPart);
                currentPart = "";
                if (partIndex >= 3) {
                    break;
                }
            }
        }
        return new Version(parts[0], parts[1], parts[2]);
    }

    public boolean isNewer(Version version) {
        if (version == null) {
            return true;
        }
        if (this.major > version.major) {
            return true;
        } else if (this.major == version.major) {
            if (this.minor > version.minor) {
                return true;
            } else if (this.minor == version.minor) {
                if (this.patch > version.patch) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return this.major + "." + this.minor + "." + this.patch;
    }
}
