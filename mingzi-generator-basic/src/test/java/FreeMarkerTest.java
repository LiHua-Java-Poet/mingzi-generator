import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FreeMarkerTest {

    @Test
    public void name() throws IOException, TemplateException {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);

        configuration.setDirectoryForTemplateLoading(new File("src/main/resources/templates"));
        configuration.setDefaultEncoding("utf-8");

        Template template = configuration.getTemplate("myweb.html.ftl");

        Map<String, Object> dataModel = new HashMap<String, Object>();
        dataModel.put("name","敏子");
        dataModel.put("complayName","敏子欢迎你");

        List<Map<String, Object>> menuItems=new ArrayList<Map<String, Object>>();
        Map<String, Object> menuItem1=new HashMap<String, Object>();
        menuItem1.put("label","Java培训");
        Map<String, Object> menuItem2=new HashMap<String, Object>();
        menuItem2.put("label","前端培训");
        Map<String, Object> menuItem3=new HashMap<String, Object>();
        menuItem3.put("label","岗前培训");

        menuItems.add(menuItem1);
        menuItems.add(menuItem2);
        menuItems.add(menuItem3);



        dataModel.put("menuItems",menuItems);
        Writer out=new FileWriter("myweb.html");
        template.process(dataModel,out);

        out.close();

    }
}
