import org.junit.Assert;
import org.junit.Test;
import value.ValueValidator;

/**
 * @author: xuemin5
 * @date: Create at 2018/12/25 20:14
 * @description:
 **/
public class valueTest {
    String def = "default";


    @Test
    public void valueCheck(){
        String testStr = "weikit";
        String re = ValueValidator.checkValue(testStr,def, ValueValidator.Validator.STRING_NOEMPTY_VA);
        Assert.assertNotNull(re);
        Assert.assertEquals(testStr,re);
    }

    @Test
    public void valueCheckEmpty(){
        String testStr = "";
        String re = ValueValidator.checkValue(testStr,def, ValueValidator.Validator.STRING_NOEMPTY_VA);
        Assert.assertNotNull(re);
        Assert.assertEquals(def,re);
    }

    @Test
    public void valueCheckNull(){
        String re = ValueValidator.checkValue(null,def, ValueValidator.Validator.STRING_NOEMPTY_VA);
        Assert.assertNotNull(re);
        Assert.assertEquals(def,re);
    }

}
