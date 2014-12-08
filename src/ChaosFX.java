import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class ChaosFX {
    static {
        try {
            addSoftwareLibrary(new File(System.getProperty("java.home") + "/lib/jfxrt.jar"));
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void initJavaFX(){}

    public static void addSoftwareLibrary(File file) throws NoSuchMethodException, MalformedURLException, InvocationTargetException, IllegalAccessException {
        Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
        method.setAccessible(true);
        method.invoke(ClassLoader.getSystemClassLoader(), new Object[]{file.toURI().toURL()});
    }
}