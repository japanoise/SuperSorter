package club.seekrit.SuperSorter;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WaifuRenamer implements Renamer {
    /*
     * https://git.sr.ht/~japanoise/waifuname
     *
     * $waifu-$num-$txt-$hash.$ext
     *
     * where:
     *
     *     $waifu is the name provided to the program (e.g. "konata")
     *     $num is a three (plus) digit number assigned in the order the files were found (e.g. "042")
     *     $txt is arbitary non-dash non-space characters, (e.g. "niceGlasses", default "nameme")
     *     $hash is the first 10 digits of the sha1sum of the file
     *     $ext is a sensible file extension (e.g. ".jpg", ".png")
     *
     */
    private String waifuName;
    private Pattern filenamePattern;

    public WaifuRenamer(String waifuName) {
        this.waifuName = waifuName.toLowerCase().replace('-', '_');
        String waifuNameEscaped = Pattern.quote(this.waifuName);
        filenamePattern = Pattern.compile("^"+waifuNameEscaped+
                "-(?<num>[0-9]+)-(?<txt>[^-]+)-(?<hash>[0-9A-Fa-f]+)\\.(?<ext>[A-Za-z0-9]+)$");
    }

    private String constructFilename(String waifu, String num, String txt, String hash, String ext) {
        return waifu + "-" + num + "-" + txt + "-" + hash + "." + ext;
    }

    @Override
    public String rename(String filename, File origFile, File destDir) {
        int i = 0;
        List<String> files = Arrays.stream(destDir.list())
                .filter(s -> new File(destDir.getPath() + Os.SEPARATOR + s).isFile())
                .sorted().collect(Collectors.toList());
        for (String file : files) {
            Matcher matcher = filenamePattern.matcher(file);
            if (matcher.find()) {
                String matchedNum = matcher.group("num");
                int matchedNumInt = Integer.parseInt(matchedNum);
                if (matchedNumInt > i) {
                    break;
                }
                i++;
            }
        }
        String num = String.format("%03d", i);

        String sha1;
        try {
            sha1 = Os.calcSHA1(origFile).substring(0, 10).toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            System.err.println("No such algorithm - serious error!\n"+ e);
            sha1 = "FFFFFFFFFF";
        } catch (IOException e) {
            System.err.println(e);
            sha1 = "FFFFFFFFFF";
        }
        String ext = Os.extension(origFile);

        i = 0;
        String desired = constructFilename(waifuName, num, "nameme", sha1, ext);
        while (new File(destDir + Os.SEPARATOR + desired).exists()) {
            desired = constructFilename(waifuName, num, "nameme_"+i, sha1, ext);
        }
        return desired;
    }
}
