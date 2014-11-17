package wph.iframework.dao.db;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xmlpull.v1.XmlSerializer;

import wph.iframework.utils.XmlUtils;

/**
 * 分页Bean
 * 
 * @author 王培鹤
 * 
 * @param <E>
 */
public class Page<E>
{
    private final static Log logger     = LogFactory.getLog(Page.class);
    private int              pageNumber = 1;                            // 当前页页码
    private int              pageSize   = 1;                            // 页大小
    private String           pageMethod;                                // 分页方法
    private int              rowCount   = 0;                            // 总行数
    private int              size       = 0;                            // 当前页大小
    private Iterator<E>      iterator   = null;                         // 迭代器
                                                                         
    public Page()
    {
    }
    
    public int getPageNumber()
    {
        if (pageNumber < 1)
        {
            pageNumber = 1;
        }
        
        return pageNumber;
    }
    
    public void setPageNumber(int pageNumber)
    {
        if (pageNumber < 1)
        {
            this.pageNumber = 1;
        }
        else
        {
            this.pageNumber = pageNumber;
        }
    }
    
    public int getPageSize()
    {
        if (pageSize < 1)
        {
            pageSize = 1;
        }
        
        return pageSize;
    }
    
    public void setPageSize(int pageSize)
    {
        if (pageSize < 1)
        {
            this.pageSize = 1;
        }
        else
        {
            this.pageSize = pageSize;
        }
    }
    
    public String getPageMethod()
    {
        return pageMethod;
    }
    
    public void setPageMethod(String pageMethod)
    {
        this.pageMethod = pageMethod == null ? "pageMethod" : pageMethod;
    }
    
    public int getPageCount()
    {
        if (rowCount > 0)
        {
            return (rowCount - 1) / pageSize + 1;
        }
        else
        {
            return 1;
        }
    }
    
    public int getRowCount()
    {
        return rowCount;
    }
    
    public void setRowCount(int rowCount)
    {
        if (rowCount < 0)
        {
            this.rowCount = 0;
        }
        else
        {
            this.rowCount = rowCount;
        }
    }
    
    /**
     * 获取开始行号
     * 
     * @return 开始行号
     */
    public int getStartRow()
    {
        if (size > 0)
        {
            return (pageNumber - 1) * pageSize + 1;
        }
        else
        {
            return 0;
        }
    }
    
    /**
     * 获取结尾行号
     * 
     * @return 结尾行号
     */
    public int getEndRow()
    {
        if (size > 0)
        {
            return (pageNumber - 1) * pageSize + size;
        }
        else
        {
            return 0;
        }
    }
    
    /**
     * 获取当前页大小
     * 
     * @return 当前页大小
     */
    public int size()
    {
        return size;
    }
    
    /**
     * 获取迭代器
     * 
     * @return 迭代器
     */
    public Iterator<E> iterator()
    {
        return iterator;
    }
    
    /**
     * 设置数据列表
     * 
     * @param list
     *            数据列表
     */
    public void setDataList(List<E> list)
    {
        if (list == null)
        {
            throw new NullPointerException();
        }
        
        if (list.size() > pageSize)
        {
            size = pageSize;
            iterator = list.subList(0, size).iterator();
        }
        else
        {
            size = list.size();
            iterator = list.iterator();
        }
    }
    
    /**
     * 获取分页控制器
     * 
     * @return 分页控制器
     */
    private String getController()
    {
        pageMethod = pageMethod == null ? "pageMethod" : pageMethod;
        StringBuffer sb = new StringBuffer();
        
        // 分页控件表格--开始
        sb.append("<table width='100%'>");
        sb.append("<tr>");
        sb.append("<td>");
        
        // 首页和上一页
        if (pageNumber > 1)
        {
            sb.append("<label onmouseover=\"style.cursor='hand'\" onclick='");
            sb.append(pageMethod);
            sb.append("(");
            sb.append(1);
            sb.append(")'>");
            sb.append("首页");
            sb.append("</label>");
            
            sb.append("　");
            
            sb.append("<label onmouseover=\"style.cursor='hand'\" onclick='");
            sb.append(pageMethod);
            sb.append("(");
            sb.append((pageNumber - 1));
            sb.append(")'>");
            sb.append("上一页");
            sb.append("</label>");
            sb.append("　");
        }
        
        // 当前页信息和总信息
        sb.append("第");
        sb.append(pageNumber);
        sb.append("页：从第");
        sb.append(getStartRow());
        sb.append("条到第");
        sb.append(getEndRow());
        sb.append("条（总共");
        sb.append(getPageCount());
        sb.append("页，");
        sb.append(getRowCount());
        sb.append("条）");
        
        // 跳至第N页
        sb.append("跳至第");
        sb.append("<select onchange='");
        sb.append(pageMethod);
        sb.append("(this.value)'>");
        for (int i = 1; i <= getPageCount(); i++)
        {
            sb.append("<option value='");
            sb.append(i);
            sb.append("'");
            
            if (pageNumber == i)
            {
                sb.append(" selected='selected'");
            }
            sb.append(">");
            sb.append(i);
            sb.append("</option>");
        }
        sb.append("</select>");
        sb.append("页");
        
        // 下一页和尾页
        if (pageNumber < getPageCount())
        {
            sb.append("　");
            sb.append("<label onmouseover=\"style.cursor='hand'\" onclick='");
            sb.append(pageMethod);
            sb.append("(");
            sb.append((pageNumber + 1));
            sb.append(")'>");
            sb.append("下一页");
            sb.append("</label>");
            
            sb.append("　");
            
            sb.append("<label onmouseover=\"style.cursor='hand'\" onclick='");
            sb.append(pageMethod);
            sb.append("(");
            sb.append(getPageCount());
            sb.append(")'>");
            sb.append("尾页");
            sb.append("</label>");
        }
        
        // 分页控件表格--结束
        sb.append("</td>");
        sb.append("</tr>");
        sb.append("</table>");
        
        return sb.toString();
    }
    
    public String toXml(String content)
    {
        content = content == null ? "" : content;
        
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
            xmlWriter.text("1");
            xmlWriter.endTag(null, "status");
            
            xmlWriter.startTag(null, "content");
            xmlWriter.text(content);
            xmlWriter.endTag(null, "content");
            
            xmlWriter.startTag(null, "controller");
            xmlWriter.text(getController());
            xmlWriter.endTag(null, "controller");
            
            xmlWriter.endTag(null, "result");
            
            // XML 结束
            xmlWriter.endDocument();
            xmlWriter.flush();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        String xml = sw.getBuffer().toString();
        logger.debug(xml);
        
        return xml;
    }
}
