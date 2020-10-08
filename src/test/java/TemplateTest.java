import com.github.lmm1990.jtemplate.FileUtil;
import com.github.lmm1990.jtemplate.TemplateUtil;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class TemplateTest {
    @Test
    void testTemplate() {
        String template = FileUtil.readAllText(this.getClass().getResourceAsStream("/test.md"));

        Map<String, Object> map = new HashMap();
        map.put("name", "张三");
        map.put("money", String.format("%.2f", 10.155));
        map.put("point", 10);
        map.put("has", true);
        map.put("list", new ArrayList<Map<String, Object>>() {{
            add(new HashMap<String, Object>() {{
                put("name", "吃饭");
                put("price", 100);
            }});
            add(new HashMap<String, Object>() {{
                put("name", "交通");
                put("price", 200);
            }});
        }});
        String result = new TemplateUtil().build(template, map);
        System.out.println(result);
        assertFalse(result.isEmpty());
    }
}
