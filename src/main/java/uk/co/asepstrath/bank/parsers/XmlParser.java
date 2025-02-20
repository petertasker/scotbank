package uk.co.asepstrath.bank.parsers;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import uk.co.asepstrath.bank.Transaction;

import java.util.List;

public class XmlParser {

    @JacksonXmlProperty(localName = "hasNext")
    private boolean hasNext;

    @JacksonXmlProperty(localName = "hasPrevious")
    private boolean hasPrevious;

    @JacksonXmlProperty(localName = "page")
    private int page;

    @JacksonXmlProperty(localName = "size")
    private int size;

    @JacksonXmlProperty(localName = "totalPages")
    private int totalPages;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "results")
    private List<Transaction> transactions;

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public boolean isHasPrevious() {
        return hasPrevious;
    }

    public void setHasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }
}
