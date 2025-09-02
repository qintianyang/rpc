package com.kk.spi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
// ExtraLoader 类用于加载 SPI 配置文件并缓存类信息
public class ExtraLoader {

    // 定义 SPI 文件路径的前缀，通常是 "META-INF/rpc/"
    public static String EXTENSION_LOADER_DIR_PREFIX = "META-INF/rpc/";

    // 用于缓存加载的类，键是接口名，值是实现类的映射
    public static Map<String, LinkedHashMap<String, Class>> EXTENSION_LOADER_CLASS_CACHE = new ConcurrentHashMap<>();

    // 加载扩展实现的方法
    public void loadExtension(Class clazz) throws IOException, ClassNotFoundException {

        // 如果传入的类为 null，抛出异常
        if (clazz == null) {
            throw new IllegalArgumentException("class is null!");
        }

        // 构建 SPI 配置文件路径，这个路径基于接口的类名
        String spiFilePath = EXTENSION_LOADER_DIR_PREFIX + clazz.getName();

        log.info("load spi file:{}", spiFilePath);

        // 获取当前类的类加载器
        ClassLoader classLoader = this.getClass().getClassLoader();

        // 获取所有资源文件路径，可能有多个配置文件
        Enumeration<URL> enumeration = classLoader.getResources(spiFilePath);

        // 遍历所有找到的 SPI 配置文件
        while (enumeration.hasMoreElements()) {
            // 获取配置文件的 URL
            URL url = enumeration.nextElement();

            // 打开 URL 流并准备读取文件内容
            InputStreamReader inputStreamReader = new InputStreamReader(url.openStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            // 逐行读取文件内容
            String line;
            // 创建一个存储实现类与接口类的映射的 Map
            LinkedHashMap<String, Class> classMap = new LinkedHashMap<>();

            // 处理每一行配置，格式为 "实现类名=接口类名"
            while ((line = bufferedReader.readLine()) != null) {
                // 跳过注释行
                if (line.startsWith("#")) {
                    continue;
                }

                // 解析配置文件中的每一行
                String[] lineArr = line.split("=");
                String implClassName = lineArr[0];  // 实现类名
                String interfaceName = lineArr[1];  // 接口类名

                // 将实现类和接口类的映射关系存储到 classMap 中
                classMap.put(implClassName, Class.forName(interfaceName));
            }

            // 将 classMap 中的实现类信息合并到缓存中
            if (EXTENSION_LOADER_CLASS_CACHE.containsKey(clazz.getName())) {
                EXTENSION_LOADER_CLASS_CACHE.get(clazz.getName()).putAll(classMap);
            } else {
                // 如果没有缓存过该接口的实现类，则创建新的缓存条目
                EXTENSION_LOADER_CLASS_CACHE.put(clazz.getName(), classMap);
            }
        }
    }
}