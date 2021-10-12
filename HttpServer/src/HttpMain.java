import java.io.IOException;

public class HttpMain {
    public static void main(String[] args) throws IOException {
        CatHttp cat = new CatHttp(8500);
        cat.startCatServer();
    }
}
