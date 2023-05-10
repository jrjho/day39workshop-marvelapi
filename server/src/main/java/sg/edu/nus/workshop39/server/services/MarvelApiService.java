package sg.edu.nus.workshop39.server.services;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import sg.edu.nus.workshop39.server.model.MarvelCharacter;

@Service
public class MarvelApiService {
    
    @Value("${workshop39.marvel.api.url}")
    private String marvelApiUrl;

    @Value("${workshop39.marvel.api.priv_key}")
    private String marvelApiPrivKey;
    
    @Value("${workshop39.marvel.api.pub_key}")
    private String marvelApiPubKey;
    
    //generate the hash required to access Marvel API and timestamp
    private String[] getMarvelApiHash(){
        String[] result = new String[2];
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        long timeStampValue = timestamp.getTime();
        String hashVal = timeStampValue + marvelApiPrivKey + marvelApiPubKey;
        result[0] = timeStampValue+"";
        result[1] = DigestUtils.md5Hex(hashVal);
        return result;
    }

    public Optional<List<MarvelCharacter>> getCharacters(String characterName,
        Integer limit, Integer offset){
        System.out.println(characterName);
        
        ResponseEntity<String> response = null;
        List<MarvelCharacter> characterArrayList = null;
        String[] resultArray = getMarvelApiHash();
        ///replace space with '+' in URL
        System.out.println(characterName.replaceAll(" ", "+"));
        //build the URL to call and access Marvel API
        String marvelApiCharsUrl = UriComponentsBuilder
                                    .fromUriString(marvelApiUrl +"characters")
                                    .queryParam("ts", resultArray[0])
                                    .queryParam("apikey", marvelApiPubKey.trim())
                                    .queryParam("hash", resultArray[1])
                                    .queryParam("nameStartsWith",characterName.replaceAll(" ", "+"))
                                    .queryParam("limit", limit)
                                    .queryParam("offset", offset)
                                    .toUriString();
        System.out.println(marvelApiCharsUrl);
        ///create rest template to get response from API
        RestTemplate restTemplate = new RestTemplate();
        response = restTemplate.getForEntity(marvelApiCharsUrl, String.class);
        try {
            System.out.println(response.getBody());
            characterArrayList= MarvelCharacter.create(response.getBody());
        } catch (IOException e) {
            e.printStackTrace();
        }                       
        
        if(characterArrayList != null)
            return Optional.of(characterArrayList);

        return Optional.empty();
    }

    public Optional<MarvelCharacter> getCharacterDetails(String charId){
        ResponseEntity<String> response = null;
        List<MarvelCharacter> characterArrayList = null;
        MarvelCharacter character = null;
        
        String[] resultArray = getMarvelApiHash();
        System.out.println(charId);
        //build the URL to call and access Marvel API
        String marvelApiCharsUrl = UriComponentsBuilder
                                    .fromUriString(marvelApiUrl +"characters/" + charId)
                                    .queryParam("ts", resultArray[0])
                                    .queryParam("apikey", marvelApiPubKey.trim())
                                    .queryParam("hash", resultArray[1])
                                    .toUriString();

        System.out.println(marvelApiCharsUrl);
        ///create rest template to get response from API
        RestTemplate restTemplate = new RestTemplate();
        response = restTemplate.getForEntity(marvelApiCharsUrl, String.class);
        try {
            characterArrayList= MarvelCharacter.create(response.getBody());
        } catch (IOException e) {
            e.printStackTrace();
        }                       
        character = characterArrayList.get(0);
        if(character != null)
            return Optional.of(character);
        return Optional.empty();
    }
}

