package uk.co.asepstrath.bank.parsers;
//
//import com.fasterxml.jackson.dataformat.xml.XmlMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.Objects;
//
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//public class XmlParserTest {
//
//    XmlMapper xmlMapper;
//
//    @BeforeEach
//    public void setUp() {
//        xmlMapper = new XmlMapper();
//    }
//
//    @Test
//    public void testXmlParserFromFile() throws IOException {
//        File xmlFile = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("transactionsxml")).getFile());
//        assertTrue(xmlFile.exists());
//        assertTrue(xmlFile.canRead());
//
//        XmlParser xmlParser = xmlMapper.readValue(xmlFile, XmlParser.class);
//
//        assertTrue(Objects.nonNull(xmlParser));
//    }
//}
