package sg.edu.nus.workshop39.server.services;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import sg.edu.nus.workshop39.server.model.Comment;
import sg.edu.nus.workshop39.server.model.MarvelCharacter;
import sg.edu.nus.workshop39.server.repository.CharCommentRepository;

@Service
public class CharacterService {
    @Autowired
    private MarvelApiService marvelApisvc;

    @Autowired
    private CharCommentRepository charCommentRepo;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    public Optional<List<MarvelCharacter>> getCharacters(String characterName, Integer limit, Integer offset){
        return this.marvelApisvc.getCharacters(characterName, limit, offset);    
    }

    public MarvelCharacter getCharacterDetails(String characterId) throws IOException{
        MarvelCharacter characterDetails = null;
        String charDetailsJson = (String)redisTemplate.opsForValue().get(characterId);
        //if character details is in redis, get from cache
        if(charDetailsJson != null)
            characterDetails = MarvelCharacter.createFromCache(charDetailsJson);
        //if character details is not in detauls, create new entry in redis and cache for 1hr
        else{
            Optional<MarvelCharacter> results = marvelApisvc.getCharacterDetails(characterId);
            characterDetails = results.get();
            redisTemplate.opsForValue().set(characterId, characterDetails.toJSON().toString());
            long currentDateTime = Instant.now().getMillis();
            Date afterAdding60Mins = new Date(currentDateTime + ( 60 * 60 * 1000));
            redisTemplate.expireAt(characterId, afterAdding60Mins);
        }
        return characterDetails;
    }

    public Comment insertComment(Comment c){
        return this.charCommentRepo.insertComment(c);
    }

    public List<Comment> getAllComments(String charId){
        return this.charCommentRepo.getAllComment(charId);
    }
}
