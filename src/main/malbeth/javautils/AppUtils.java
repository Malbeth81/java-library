package malbeth.javautils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class AppUtils {
    public static List<String> getPackageClasses(String packageName) {
        ArrayList<String> classes = new ArrayList<String>();
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader != null) {
                Enumeration<URL> resources = classLoader.getResources(packageName.replace(".", "/"));
                while (resources.hasMoreElements())
                    classes.addAll(findClasses(URLDecoder.decode(resources.nextElement().getFile(), "UTF-8"), packageName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return classes;

    }

    public static boolean loadPackage(String packageName) {
        if (packageName != null) {
            List<String> urls = getPackageClasses(packageName);
            if (urls.size() > 0) {
                boolean result = true;
                for (ListIterator<String> it = urls.listIterator(); it.hasNext(); ) {
                    try {
                        Class.forName(it.next());
                    } catch (Exception e) {
                        e.printStackTrace();
                        result = false;
                    }
                }

                return result;
            }
        }

        return false;

    }

    public static boolean loadPackages(String packageNames) {
        if (packageNames != null) {
            boolean result = true;

            String[] packages = packageNames.split("[,; ]+");
            for (int i = 0; i < packages.length; i++)
                result = result && loadPackage(packages[i]);

            return result;
        }

        return false;
    }

    public static Properties loadProperties(String fileName) {
        if (fileName != null) {
            Properties result = new Properties();

            try {
                InputStream stream = AppUtils.class.getResourceAsStream(fileName);
                if (stream != null)
                    result.load(stream);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        return null;
    }

    private static TreeSet<String> findClasses(String directory, String packageName) {
        TreeSet<String> classes = new TreeSet<String>();
        if (directory != null && directory.length() > 0 && packageName != null && packageName.length() > 0) {
            directory = directory.replace("%20", " ");
            try {
                if (directory.startsWith("file:") && directory.contains("!")) {
                    ZipInputStream zip = new ZipInputStream(new URL(directory.split("!")[0]).openStream());
                    for (ZipEntry entry = null; (entry = zip.getNextEntry()) != null; )
                        if (!entry.isDirectory() && entry.getName().endsWith(".class") && entry.getName().startsWith(packageName.replace(".", "/")))
                            classes.add(entry.getName().replaceAll("[.]class", "").replace("/", "."));
                } else {
                    for (File file : new File(directory).listFiles()) {
                        String fileName = file.getName();
                        if (file.isFile() && fileName.endsWith(".class"))
                            classes.add(packageName + '.' + fileName.substring(0, fileName.length() - 6));
                        else if (file.isDirectory() && !fileName.contains("."))
                            classes.addAll(findClasses(file.getAbsolutePath(), packageName + "." + fileName));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return classes;
    }

}
