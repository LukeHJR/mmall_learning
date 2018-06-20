//package com.mmall.common;
//
//import org.mybatis.generator.api.GeneratedXmlFile;
//import org.mybatis.generator.api.dom.xml.Document;
//import org.mybatis.generator.codegen.mybatis3.IntrospectedTableMyBatis3Impl;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @title
// * @Author huangjiarui
// * @date: 2018-06-20
// */
//@Component
//public abstract class GetGeneratedXmlFiles extends IntrospectedTableMyBatis3Impl {
//    @Override
//    public List<GeneratedXmlFile> getGeneratedXmlFiles() {
//        List<GeneratedXmlFile> answer = new ArrayList<GeneratedXmlFile>();
//
//        if (xmlMapperGenerator != null) {
//            Document document = xmlMapperGenerator.getDocument();
//        /**
//         * 原来中的 GeneratedXmlFile 保留；将其中构造函数中的true 修改为 ： false;
//         * 设置 isMergeable = false； 在生成 xml文件的时候，将不是合并，而是直接覆盖；
//         */
//        /*GeneratedXmlFile gxf = new GeneratedXmlFile(document,
//        getMyBatis3XmlMapperFileName(), getMyBatis3XmlMapperPackage(),
//        context.getSqlMapGeneratorConfiguration().getTargetProject(),
//        true, context.getXmlFormatter());*/
//            String tmp = context.getProperty("mergeable");
//            boolean mergeable = false;
//            if ("true".equalsIgnoreCase(tmp)) {
//                mergeable = true;
//            }
//            GeneratedXmlFile gxf = new GeneratedXmlFile(document,
//                    getMyBatis3XmlMapperFileName(), getMyBatis3XmlMapperPackage(),
//                    context.getSqlMapGeneratorConfiguration().getTargetProject(),
//                    mergeable, context.getXmlFormatter());
//            if (context.getPlugins().sqlMapGenerated(gxf, this)) {
//                answer.add(gxf);
//            }
//        }
//
//        return answer;
//    }
//}
