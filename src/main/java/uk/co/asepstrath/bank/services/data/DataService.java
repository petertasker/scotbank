package uk.co.asepstrath.bank.services.data;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.List;

public interface DataService<T> {
    List<T> fetchData() throws IOException, XMLStreamException;
}
