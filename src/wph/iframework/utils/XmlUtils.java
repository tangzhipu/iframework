package wph.iframework.utils;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlSerializer;

/**
 * Xml工具
 * 
 * @author 王培鹤
 * 
 */
public final class XmlUtils
{
    private XmlUtils()
    {
    }
    
    /**
     * 使用SAX方式解析xml
     * 
     * @param xml
     * @param handler
     * @throws Exception
     */
    public static void saxParse(String xml, DefaultHandler handler) throws Exception
    {
        if (xml == null || handler == null)
        {
            throw new NullPointerException();
        }
        // 构建SAXParser
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        // 构建InputSource
        InputSource is = new InputSource(new StringReader(xml));
        // 调用parse()方法
        parser.parse(is, handler);
    }
    
    /**
     * 将Node转化为xml
     * 
     * @param node
     * @return
     */
    public static String toXml(Node node)
    {
        if (node == null)
        {
            return null;
        }
        
        // 获得TransformerFactory实例
        TransformerFactory transFactory = TransformerFactory.newInstance();
        
        // 生成Transformer
        Transformer transformer = null;
        try
        {
            transformer = transFactory.newTransformer();
            // 字符编码采用UTF-8
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            // 设置为独立的
            transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        }
        catch (TransformerConfigurationException e)
        {
            e.printStackTrace();
            return null;
        }
        
        // 生成DOMSource
        DOMSource domSource = new DOMSource(node);
        // 生成StringWriter
        StringWriter writer = new StringWriter();
        // 生成Result
        Result result = new StreamResult(writer);
        
        // 转化xml
        try
        {
            transformer.transform(domSource, result);
        }
        catch (TransformerException e)
        {
            e.printStackTrace();
            return null;
        }
        
        // 得到xml字符串
        return writer.getBuffer().toString();
    }
    
    /**
     * 生成文本Element
     * 
     * @param document
     *            Document对象
     * @param tag
     *            标签
     * @param text
     *            文本
     * @return 若参数都不为空，则返回带有文本的Element，否则返回null。
     */
    public static Element createTextElement(Document document, String tag, String text)
    {
        if (document == null || tag == null || text == null)
        {
            return null;
        }
        
        Element element = document.createElement(tag);
        Text textNode = document.createTextNode(text);
        element.appendChild(textNode);
        return element;
    }
    
    /**
     * 向节点node添加子节点child
     * 
     * @param node
     *            节点
     * @param child
     *            子节点
     */
    public static void appendChild(Node node, Node child)
    {
        if (node != null && child != null)
        {
            node.appendChild(child);
        }
    }
    
    /**
     * 获取XML序列化工具
     * 
     * @return
     */
    public static XmlSerializer getXmlSerializer()
    {
        XmlSerializer serializer = new XmlWriter();
        return serializer;
    }
    
    public static String buildXmlReturnValue(int status, String message)
    {
        message = message == null ? "" : message;
        
        // XML 代码生成工具
        XmlSerializer xmlWriter = XmlUtils.getXmlSerializer();
        StringWriter sw = new StringWriter();
        try
        {
            xmlWriter.setOutput(sw);
            
            // XML 开始
            xmlWriter.startDocument("UTF-8", true);
            
            // result 标签
            xmlWriter.startTag(null, "result");
            // status 标签
            xmlWriter.startTag(null, "status");
            xmlWriter.text(status + "");
            xmlWriter.endTag(null, "status");
            
            xmlWriter.startTag(null, "message");
            xmlWriter.text(message);
            xmlWriter.endTag(null, "message");
            
            xmlWriter.endTag(null, "result");
            
            // XML 结束
            xmlWriter.endDocument();
            xmlWriter.flush();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return sw.getBuffer().toString();
    }
}
