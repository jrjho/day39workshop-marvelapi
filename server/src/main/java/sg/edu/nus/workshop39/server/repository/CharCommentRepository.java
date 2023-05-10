package sg.edu.nus.workshop39.server.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import sg.edu.nus.workshop39.server.model.Comment;

@Repository
public class CharCommentRepository {
    @Autowired
    private MongoTemplate template;

    private static final String COMMENTS_COL = "comments";

    public Comment insertComment(Comment c){
        return this.template.insert(c, COMMENTS_COL);
    }

    public List<Comment> getAllComment(String charId){
        Pageable pageable = PageRequest.of(0, 10);
        Query commentsDynamicQry = new Query()
                .addCriteria(Criteria.where("charId").is(charId))
                .with(pageable);

        List<Comment> filterComments = template.find(commentsDynamicQry, Comment.class,COMMENTS_COL);
        Page<Comment> commentPage = PageableExecutionUtils.getPage(
                    filterComments, 
                    pageable, 
                    () -> template.count(commentsDynamicQry, 
                            Comment.class));
        return commentPage.toList();
                        
    } 
}
