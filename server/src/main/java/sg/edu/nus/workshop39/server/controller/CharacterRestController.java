package sg.edu.nus.workshop39.server.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import sg.edu.nus.workshop39.server.model.Comment;
import sg.edu.nus.workshop39.server.model.MarvelCharacter;
import sg.edu.nus.workshop39.server.services.CharacterService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(path="/api/characters")
public class CharacterRestController {
    
    @Autowired
    private CharacterService charSvc;

    @GetMapping
    public ResponseEntity<String> getCharacters(@RequestParam(required=true) String charName, @RequestParam(required=true) Integer limit, @RequestParam(required=true) Integer offset){
        System.out.println("char >" + charName);
        

        Optional<List<MarvelCharacter>> resultOptionalArrayList = this.charSvc.getCharacters(charName, limit, offset);
        List<MarvelCharacter> resultArrayList = resultOptionalArrayList.get();
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        //append result to json array
        for(MarvelCharacter c: resultArrayList)
            jsonArrayBuilder.add(c.toJSON());

        JsonArray result = jsonArrayBuilder.build();
        //return result as json array
        System.out.println("Results is: "+result.toString());
        return ResponseEntity.status(HttpStatus.OK)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(result.toString());
    }

    @GetMapping(path="/{charId}")
    public ResponseEntity<String> getCharacterDetails(@PathVariable(required=true) String charId) throws IOException{

        System.out.println(charId);
        MarvelCharacter character = this.charSvc.getCharacterDetails(charId);

        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder()
                                                  .add("details", character.toJSON());

        JsonObject jsonResult = jsonObjectBuilder.build();
        System.out.println("Results is: "+jsonResult.toString());
        return ResponseEntity.status(HttpStatus.OK)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(jsonResult.toString());
    }

    @PostMapping(path="/{charId}")
    public ResponseEntity<String> saveCharacterComment(@RequestBody Comment comment, @PathVariable(required=true) String charId){
        System.out.println("save Comment!");
        System.out.println("charId? " + charId);
        System.out.println("comment? " + comment.getCharId());
        comment.setComment(comment.getComment());
        comment.setCharId(charId);
        Comment commentObj= this.charSvc.insertComment(comment);

        return ResponseEntity.status(HttpStatus.OK)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(commentObj.toString());
    }

    @GetMapping(path="/comments/{charId}")
    public ResponseEntity<String> getCharComments(@PathVariable(required=true) String charId){
        List<Comment> commentArrayList=  this.charSvc.getAllComments(charId);
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        
        for(Comment comment : commentArrayList)
        jsonArrayBuilder.add(comment.toJSON());

        JsonArray resultArray =jsonArrayBuilder.build();

        return ResponseEntity.status(HttpStatus.OK)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(resultArray.toString());
    }
}
