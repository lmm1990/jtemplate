import com.github.lmm1990.jtemplate.FileUtil;
import com.github.lmm1990.jtemplate.TemplateUtil;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TemplateTest {

    private Map<String, Object> map = new HashMap() {{
        put("name", "张三");
        put("money", String.format("%.2f", 10.155));
        put("point", 10);
        put("has", true);
        put("sex", 0);
        put("list", new ArrayList<Map<String, Object>>() {{
            add(new HashMap<String, Object>() {{
                put("name", "吃饭");
                put("price", 100);
            }});
            add(new HashMap<String, Object>() {{
                put("name", "交通");
                put("price", 200);
            }});
        }});
    }};

    @Test
    void testTemplate() {
        String template = FileUtil.readAllText(this.getClass().getResourceAsStream("/test.md"));
        String result = new TemplateUtil(template).build(map);
        System.out.println(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testIfTemplate() {
        String template = FileUtil.readAllText(this.getClass().getResourceAsStream("/test.md"));
        String result = new TemplateUtil(template).build(new HashMap<>(map){{
            put("has", false);
            put("sex", 1);
        }});
        System.out.println(result);
        assertTrue(result.contains("男"));
    }

    @Test
    void testElseIfTemplate() {
        String template = FileUtil.readAllText(this.getClass().getResourceAsStream("/test.md"));
        String result = new TemplateUtil(template).build(new HashMap<>(map){{
            put("has", false);
            put("sex", 2);
        }});
        System.out.println(result);
        assertTrue(result.contains("女"));
    }

    @Test
    void testElseTemplate() {
        String template = FileUtil.readAllText(this.getClass().getResourceAsStream("/test.md"));
        String result = new TemplateUtil(template).build(new HashMap<>(map){{
            put("has", false);
        }});
        System.out.println(result);
        assertTrue(result.contains("未知"));
    }
}
