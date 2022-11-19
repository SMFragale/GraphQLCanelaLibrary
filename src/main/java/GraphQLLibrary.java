import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class GraphQLLibrary {

    public static String queryGraphQLService(String url, String operation, String query) throws URISyntaxException,
            IOException, GraphQLException {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        URI uri = new URIBuilder(request.getURI())
                .addParameter("query", query)
                .build();
        request.setURI(uri);
        HttpResponse response =  client.execute(request);
        InputStream inputResponse = response.getEntity().getContent();
        String actualResponse = new BufferedReader(
                new InputStreamReader(inputResponse, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

        final ObjectNode node = new ObjectMapper().readValue(actualResponse, ObjectNode.class);
        if(node.get("data") == null)
            throw new GraphQLException(node.toString());

        return node.get("data").get(operation).toString();
    }

    public static String mutateGraphQLService(String url, String operation, String query) throws URISyntaxException,
            IOException {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(url);
        URI uri = new URIBuilder(request.getURI())
                .addParameter("query", query)
                .build();
        request.setURI(uri);
        HttpResponse response =  client.execute(request);
        InputStream inputResponse = response.getEntity().getContent();
        String actualResponse = new BufferedReader(
                new InputStreamReader(inputResponse, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

        final ObjectNode node = new ObjectMapper().readValue(actualResponse, ObjectNode.class);
        if(node.get("data") == null)
            return node.toString();

        return node.get("data").get(operation).toString();
    }

}

