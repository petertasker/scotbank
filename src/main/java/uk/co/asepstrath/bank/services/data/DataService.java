package uk.co.asepstrath.bank.services.data;

public class DataService {

    protected UnirestWrapper unirestWrapper;

    public DataService() {
        this.unirestWrapper = new UnirestWrapper();
    }

    public DataService(UnirestWrapper unirestWrapper) {
        this.unirestWrapper = unirestWrapper;
    }

    public UnirestWrapper getUnirestWrapper() {
        return this.unirestWrapper;
    }
}