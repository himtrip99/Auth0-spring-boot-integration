package com.auth0.app.controllers;


import com.auth0.app.model.Message;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping(path = "api", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*")
public class APIController {

    @Value(value = "${com.auth0.clientId}")
    private String clientId;

    @Value(value = "${com.auth0.clientSecret}")
    private String clientSecret;

    @Value(value = "${auth0.management.api.get.actions.url}")
    private String getActionsUrl;


    @GetMapping(value = "/public")
    public Message publicEndpoint() {
        return new Message("All good. You DO NOT need to be authenticated to call /api/public.");
    }

    @GetMapping(value = "/private")
    public Message privateEndpoint() {
        return new Message("All good. You can see this because you are Authenticated.");
    }

    @GetMapping(value = "/private-scoped")
    public Message privateScopedEndpoint() {
        return new Message("All good. You can see this because you are Authenticated with a Token granted the 'read:messages' scope");
    }

    @GetMapping(value="/getApplicationActions" , produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> getApplicationActions(HttpServletRequest request, HttpServletResponse response) throws JsonParseException, JsonProcessingException {
        ResponseEntity<String> result = getCall(getActionsUrl);
        JSONObject actions = new JSONObject(result.getBody());
        JSONArray array = actions.getJSONArray("actions");
        HashMap<String, Map<String,String>> respJson = new HashMap<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            Map appInfo = new HashMap();
            appInfo.put("action_name",object.getString("name"));
            Scanner scanner = new Scanner(object.getString("code"));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if(line.contains("event.client.name")){
                    Matcher matcher = Pattern.compile("[\"'](.+)[\"']").matcher(line);
                    if (matcher.find()) {
                        appInfo.put("application_name",matcher.group(1));
                    }
                }
            }

            appInfo.put("supported_triggers", object.getJSONArray("supported_triggers").getJSONObject(0).getString("id"));
            respJson.put(Integer.toString(i),appInfo);
            scanner.close();
        }
        return new ResponseEntity<String>(respJson.toString(), HttpStatus.OK);
    }

    public String getAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject requestBody = new JSONObject();
        requestBody.put("client_id", clientId);
        requestBody.put("client_secret", clientSecret);
        requestBody.put("audience", "https://dev-n29jqgiv.us.auth0.com/api/v2/");
        requestBody.put("grant_type", "client_credentials");

        HttpEntity<String> request = new HttpEntity<String>(requestBody.toString(), headers);

        RestTemplate restTemplate = new RestTemplate();
        HashMap<String, String> result = restTemplate.postForObject("https://dev-n29jqgiv.us.auth0.com/oauth/token", request, HashMap.class);

        return result.get("access_token");
    }

    public ResponseEntity<String> getCall(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer "+getAccessToken());

        HttpEntity<String> entity = new HttpEntity<String>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return result;
    }

}