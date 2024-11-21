import one.empty3.library.Lumiere;
import org.junit.Test;

public class TestLumiere {
    @Test
    public void testLumiereGetInt( ) {
        int anInt = Lumiere.getInt(1f, 0f, 0f);
        System.out.println(Lumiere.getColor(anInt).toString());
    }
}
