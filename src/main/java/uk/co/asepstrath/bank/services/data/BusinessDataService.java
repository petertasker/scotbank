package uk.co.asepstrath.bank.services.data;

import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.asepstrath.bank.Business;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BusinessDataService implements DataService<Business> {

    private UnirestWrapper unirestWrapper;


    public BusinessDataService() {
        Logger logger = LoggerFactory.getLogger(BusinessDataService.class);
        this.unirestWrapper = new UnirestWrapper();
    }

    public void setUnirestWrapper(UnirestWrapper wrapper) {
        this.unirestWrapper = wrapper;
    }

    @Override
    public List<Business> fetchData() throws IOException {
        try {
            HttpResponse<String> response = unirestWrapper.get("https://api.asep-strath.co.uk/api/businesses");

            if (!response.isSuccess()) {
                throw new IOException("Failed to fetch businesses: " + response.getStatus());
            }

            List<Business> businesses = new ArrayList<>();
            String[] lines = response.getBody().split("\n");
            boolean skipHeader = true;

            for (String inputLine : lines) {
                if (skipHeader){
                    skipHeader = false;
                    continue;
                }

                String[] inputFields = inputLine.split(",");
                if (inputFields.length >= 4) {
                    String id = inputFields[0].trim();
                    String name = inputFields[1].trim();
                    String category = inputFields[2].trim();
                    boolean sanctioned = Boolean.parseBoolean(inputFields[3].trim());

                    businesses.add(new Business(id, name, category, sanctioned));
                }
            }
            return businesses;

        } catch (IOException e) {
            throw new IOException("Failed to parse business data: ", e);
        }
    }
}
