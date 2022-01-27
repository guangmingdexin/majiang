package ds.guang.majing.common.util;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @ClassName ClassUtil
 * @Description 操作 Class 工具类
 * @Author guangmingdexin
 * @Date 2021/4/11 10:40
 * @Version 1.0
 **/
public final class ClassUtil {

    private static final char PACKAGE_SEPARATOR_CHAR = '.';
    /**
     * 获取类上的泛型参数
     * 不支持嵌套类型参数 例如 List<List<String>>
     *
     * @param instance 对象实例
     * @param clazz 获取类的泛型参数类型
     * @param parameterIndex 参数索引
     * @return 泛型参数类型
     */
    public static Class<?> findSubClassParameterType(Object instance, Class<?> clazz, int parameterIndex) {
        Map<Type, Type> typeMap = new HashMap<>();
        Class<?> subClass = instance.getClass();
        while (subClass.getSuperclass() != clazz) {
            extractTypeArguments(typeMap, subClass);
            // 循环判断 一直到最顶层 类
            subClass = subClass.getSuperclass();
            if(subClass == null) {
                throw new IllegalArgumentException("类型不匹配！");
            }
        }
        ParameterizedType pt = (ParameterizedType) subClass.getGenericSuperclass();

        Type actualType = pt.getActualTypeArguments()[parameterIndex];
        if(typeMap.containsKey(actualType)) {
            actualType = typeMap.get(actualType);
        }

        if(actualType instanceof Class) {
            return (Class<?>) actualType;
        }else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * 获取接口的泛型参数
     * 不支持嵌套类型参数 例如 List<List<String>>
     *
     * @param instance 对象实例
     * @param clazz 获取泛型参数的接口类型
     * @param interfaceIndex 实现的接口索引
     * @param parameterIndex 参数索引
     * @return 泛型参数类型
     */
    public static Class<?> findInterfaceParameter(Object instance, Class<?> clazz, int interfaceIndex, int parameterIndex) {
        // 首先需要判断 clazz 的类型
        if(!clazz.isInterface()) {
            throw new IllegalArgumentException("类型错误！");
        }
        Class<?> instanceClass = instance.getClass();
        // 判断 对象 是否为该接口类型
        if(clazz.isAssignableFrom(instanceClass)) {
            // 多个接口泛型类型
            Type[] types = instanceClass.getGenericInterfaces();
            //
            Type t = types[interfaceIndex];
            Type[] actualType = ((ParameterizedType) t).getActualTypeArguments();

            if(actualType[parameterIndex] instanceof Class) {
                return (Class<?>) actualType[parameterIndex];
            }
        }
        throw new IllegalArgumentException("类型错误！");
    }

    private static void extractTypeArguments(Map<Type, Type> typeMap, Class<?> clazz) {
        // 获取类上面的 泛型类型
        Type genericSuperclass = clazz.getGenericSuperclass();
        if(!(genericSuperclass instanceof ParameterizedType)) {
            return;
        }

        ParameterizedType pt = (ParameterizedType)genericSuperclass;
        // getRawType() 返回参数化类型中的原始类型
        // 例如 List<String>的原始类型为 List
        // getTypeParameters() 返回 类上面的 泛型名称
        // 比如 List.class.getTypeParameters(); 返回 E
        Type[] typeParameters = ((Class<?>) pt.getRawType()).getTypeParameters();
        // 获取 父类所有的泛型类型参数
        Type[] actualTypeArguments = pt.getActualTypeArguments();

        for (int i = 0; i < typeParameters.length; i++) {
            // 内部逻辑
            // Type(String)  ----->  Type(U) ------------->  Type(T)
            // (typeParameters)U  ------------  (Actual)String
            //
            // (typeParameters)T ------------- (Actual)String
            if(typeMap.containsKey(actualTypeArguments[i])) {
                actualTypeArguments[i] = typeMap.get(actualTypeArguments[i]);
            }
            typeMap.put(typeParameters[i], actualTypeArguments[i]);
        }

    }

    /**
     * @param packageName 包名
     * @param target 注解类
     * @param recursive 是否递归遍历
     * @return 存在 target 注解的类
     */
    public static Set<Class<?>> getClassFromPath(String packageName, Class target, boolean recursive) {

        String packageDirName = packageName.replace('.', File.separatorChar);
        Set<Class<?>> classes = new HashSet<>();
        Set<Class<?>> source = new HashSet<>();
        try {
            // 获取当前 classpath 的绝对路径 uri
            Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                // 获取下一个元素
                URL url = dirs.nextElement();
                // 得到协议的名称
                String protocol = url.getProtocol();
                // 如果是以文件的形式保存在服务器上
                if("file".equals(protocol)) {
                    // 获取文件路径
                    String filePath = URLDecoder.decode(url.getPath(), "UTF-8");
                    // 构建文件对象
                    File f = new File(filePath);
                    // 如果不存在或者不是目录，直接返回
                    if(!f.exists() || !f.isDirectory()) {
                        return source;
                    }
                    getClassFile(classes, f, packageName, true);

                }else if("jar".equals(protocol)) {
                    // 如果是 jar 文件
                    // 定义一个 jarFile
                    JarFile jarFile = ((JarURLConnection) url.openConnection()).getJarFile();
                    // 从此 jar 文件 获得一个枚举类
                    Enumeration<JarEntry> entries = jarFile.entries();
                    // 循环迭代
                    while (entries.hasMoreElements()) {
                        // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                        JarEntry jarEntry = entries.nextElement();
                        String name = jarEntry.getName();
                        // 如果是以/开头的
                        if (name.charAt(0) == '/') {
                            // 获取后面的字符串
                            name = name.substring(1);
                        }
                        // 如果前半部分和定义的包名相同
                        if (name.startsWith(packageDirName)) {
                            int idx = name.lastIndexOf('/');
                            // 如果以"/"结尾 是一个包
                            if (idx != -1) {
                                // 获取包名 把"/"替换成"."
                                packageName = name.substring(0, idx).replace('/', '.');
                            }
                            // 如果可以迭代下去 并且是一个包
                            if ((idx != -1) || recursive) {
                                // 如果是一个.class文件 而且不是目录
                                if (name.endsWith(".class") && !jarEntry.isDirectory()) {
                                    // 去掉后面的".class" 获取真正的类名
                                    String className = name.substring(packageName.length() + 1, name.length() - 6);
                                    try {
                                        // 添加到classes
                                        classes.add(Class.forName(packageName + '.' + className));
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 获取 特定类型的 Class
        for (Class<?> clazz : classes) {
            if (clazz.getAnnotation(target) != null) {
                // 说明该类存在此注解
                source.add(clazz);
            }
        }
        return source;
    }

    /**
     * @param classFiles 结果集合
     * @param f 文件对象
     * @param packageName 包名
     * @param recursive 是否递归扫描
     */
    private static void getClassFile(Set<Class<?>> classFiles, File f, String packageName, boolean recursive) {

        // 如果存在就获取包下面的所有目录
        File[] files = f.listFiles(new FileFilter() {
            // 自定义过滤规则
            // 首先判断是否循环获取
            // 获取 class 文件
            @Override
            public boolean accept(File pathname) {
                return (recursive && pathname.isDirectory() || (pathname.getName().endsWith(".class")));
            }
        });
        if(files == null || files.length == 0) {
            return;
        }
        for (File file : files) {
            // 如果是目录继续扫描
            if(file.isDirectory()) {
                getClassFile(classFiles, file, packageName, recursive);
            }else {

                try {
                    // 添加到 集合
                    // TODO 如果 class 上还有包会出现错误
                    // 1.获取该 class 文件的绝对路径
                    // 2.截取从 packageName 开始的文件路径

                    String absolutePath = file.getAbsolutePath();
                    // 使用正则表达式 将文件中的所有特殊字符转变为 .
                    String regEx="[\n`~!@#$%^&*()+=|{}':;',\\[\\].\\\\<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。， 、？]";
                    absolutePath = absolutePath.replaceAll(regEx, ".");
                    int startIndex = absolutePath.indexOf(packageName);
                    // 是 class 文件
                    String className = absolutePath.substring(startIndex, absolutePath.length() - 6);
                    // 可以再加一个参数校验, 防止一些第三方框架影响使用，
                    // 比如：jackson 框架在进行泛型对象转换时，会动态的生成一些增强类，影响创建实例
                    classFiles.add(Class.forName(className));
                } catch (ClassNotFoundException e) {
                  // e.printStackTrace();
                }
            }
        }
    }



    public static Object mapToObj(Map<String, Object> map, Class<?> clazz) {
        try {
            Object o = clazz.newInstance();
            // 获取所有属性名
            Field[] fields = clazz.getDeclaredFields();

            Map<String, Method> allMethods = getAllMethods(clazz);
            for (Field field : fields) {
                String name = field.getName();
                if(map.containsKey(name)) {
                    String setMethod = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
                    Method method = allMethods.get(setMethod);
                    if(method != null) {
                        method.invoke(o, map.get(name));
                    }
                }
            }

            return o;
        } catch (InstantiationException | IllegalAccessException |  InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Map<String, Method> getAllMethods(Class<?> clazz) {
        Method[] methods = clazz.getMethods();
        Map<String, Method> map = new HashMap<>(methods.length);
        for (Method method : methods) {
            String name = method.getName();
            map.put(name, method);
        }
        return map;
    }

    /**
     * 判断 某个对象上是否存在某个注解
     *
     * @param obj
     * @param anno
     * @return
     */
    public static boolean exits(Object obj, Class<? extends Annotation> anno) {
        return exits(obj.getClass(), anno);
    }

    public static boolean exits(Class<?> clazz, Class<? extends Annotation> anno) {
        Annotation annotation = clazz.getAnnotation(anno);
        return annotation != null;
    }

    public static Object copyObj(Object obj) {

        try {
            ByteArrayOutputStream array = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(array);
            oos.writeObject(obj);
            oos.close();

            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(array.toByteArray()));
            return ois.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static  <T> T convert(Object obj, Class<T> clazz) {

        Objects.requireNonNull(obj, "convert class don't null");

        try {
            return (T)obj;
        }catch (ClassCastException e) {
            // 打印
            System.out.println("类型转换错误！");
        }
        return null;
    }

    public static String simpleClassName(Class<?> clazz) {
        if (clazz == null) {
            throw new NullPointerException("clazz");
        }
        String className = clazz.getName();
        final int lastDotIdx = className.lastIndexOf(PACKAGE_SEPARATOR_CHAR);
        if (lastDotIdx > -1) {
            return className.substring(lastDotIdx + 1);
        }
        return className;
    }
}
