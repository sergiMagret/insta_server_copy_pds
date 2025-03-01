package org.udg.pds.springtodo.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.udg.pds.springtodo.controller.exceptions.ServiceException;
import org.udg.pds.springtodo.entity.*;
import org.udg.pds.springtodo.service.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.util.*;

@RequestMapping(path="/publications")
@RestController
public class PublicationController  extends BaseController{
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    @Autowired
    PublicationService publicationService;
    @Autowired
    UserService userService;
    @Autowired
    CommentService commentService;
    @Autowired
    HashtagService hashtagService;

    @GetMapping
    @JsonView(Views.Public.class)
    public Collection<Publication> listAllPublications(HttpSession session,
                                                       @RequestParam Integer page, @RequestParam Integer size) {
        Long userId = getLoggedUser(session);
        Collection<User> llista=userService.getFollowed(userId);
        llista.add(userService.getUser(userId));
        return publicationService.publicationsFollowed(llista,page,size);
    }



    @GetMapping(path="/{id}")
    public Publication getPublication(HttpSession session, @PathVariable("id") Long publicationId) {
        getLoggedUser(session);
        Optional<Publication> op = publicationService.crud().findById(publicationId);
        if(!op.isPresent()){
            throw new ServiceException("Publication does not exist!");
        }
        return op.get();
    }

    @GetMapping(path="/{id}/likes")
    public List<Integer> getLikes(HttpSession session, @PathVariable("id") Long publicationId) {
        Long userId = getLoggedUser(session);
        List<Integer> l = new ArrayList<Integer>();
        Optional<Publication> op = publicationService.crud().findById(publicationId);
        if(!op.isPresent()){
            throw new ServiceException("Publication does not exist!");
        }
        l.add(op.get().getLikes());
        Optional<User> ou = userService.crud().findById(userId);
        if(!ou.isPresent()) throw new ServiceException("User does not exist!");
        if(op.get().hasLiked(ou.get())){
            l.add(1);
        }
        else{
            l.add(0);
        }
        return l;
    }

    @GetMapping(path="/{id}/tagged")
    public Collection<User> getTaggedUsers(HttpSession session, @PathVariable("id") Long publicationId){
        Optional<Publication> op = publicationService.crud().findById(publicationId);
        if(!op.isPresent()) throw new ServiceException("User does not exist!");
        return op.get().getTaggedUsers();
    }

    @GetMapping(path="/{id}/nComments")
    public Integer getNumComments(HttpSession session, @PathVariable("id") Long publicationId) {
        int n = 0;
        Optional<Publication> op = publicationService.crud().findById(publicationId);
        if(!op.isPresent()){
            throw new ServiceException("Publication does not exist!");
        }
        n = op.get().getNComments();
        return n;
    }


    @GetMapping(path="/{id}/comments")
    public Collection<Comment> getComments(HttpSession session, @PathVariable("id") Long publicationId, @RequestParam Integer page, @RequestParam Integer size) {
        getLoggedUser(session);

        Optional<Publication> op = publicationService.crud().findById(publicationId);
        if(!op.isPresent()){
            throw new ServiceException("Publication does not exist!");
        }
        return commentService.getComments(publicationId,page,size);
    }

    @PostMapping(path="/{publicationId}/{username}/tag")
    public Integer tagUser(HttpSession session, @PathVariable("publicationId") Long publicationId, @PathVariable("username") String userName){
        return publicationService.tagUser(userName, publicationId);
    }

    @PostMapping(path="/{id}/comments")
    @JsonView(Views.Private.class)
    public String addComment(HttpSession session, @Valid @RequestBody CommentPost comment){
        Long userId = this.getLoggedUser(session);
        User u = userService.getUser(userId);
        Publication p = publicationService.getPublication(comment.publicationId);
        Date currentDate = new Date();
        Comment c = new Comment(comment.text);
        c.setPublication(p);
        c.setUser(u);
        c.setDate(currentDate);
        commentService.addComment(c);
        NotificationRequest request = new NotificationRequest();
        User u2 = p.getUser();
        if(u2.getToken() != null) { // If the token is null that means the user hasn't signed in to the app
            request.target = u2.getToken();
            request.title = "You have a new comment!";
            request.body = "The user " + u.getName() + " has commented your publication!";
            String response = NotificationService.getInstance().sendNotification(request);
            System.out.println(response);
        }else{
            System.out.println("Can't send the notification, the token is null!");
        }
        return BaseController.OK_MESSAGE;
    }



    @PostMapping(path="/{id}/like")
    public Publication addLike(HttpSession session, @PathVariable("id") Long publicationId){
        Long userId = this.getLoggedUser(session);
        User u = userService.getUser(userId);
        Publication pb = publicationService.addLike(userId, publicationId);
        User u2 = pb.getUser();
        NotificationRequest request = new NotificationRequest();
        if(u.getToken() != null){
            if(u2.getId() != userId) { // Don't notificate self-likes
                request.target = u.getToken();
                request.title = "You have a new like!";
                request.body = "The user " + u.getName() + " has liked your publication";
                String response = NotificationService.getInstance().sendNotification(request);
                System.out.println(response);
            }
        }else{
            System.out.println("Can't send the notification, the token is null");
        }
        return pb;
    }


    @PostMapping (consumes = "application/json")
    @JsonView(Views.Private.class)
    public Long postPublication (HttpSession session,@Valid @RequestBody PublicationPost pub){
        Publication p = new Publication(pub.photo, pub.description, pub.date);
        Long loggedUserId = getLoggedUser(session);
        User u = userService.getUser(loggedUserId);
        p.setUser(u);
        u.addPublication(p);
        publicationService.addPublication(p);

        List<String> hashtags = searchForHashtags(pub.description);
        for(String name: hashtags){ // For every hashtag that have been found in the description
            // Since it's a many to many relationship between hashtag and publication, if you add the
            // publication to the list of publications that the hashtag has, it will automatically appear
            // on the other side of the relationship, than means in the list of hashtags of the publication.
            // Because of that you only have to add it to one side.
            try{
                Hashtag h = hashtagService.getHashtagByName(name);
                publicationService.addHashtagTo(p, h);
            }catch(ServiceException ex){
                Hashtag h = hashtagService.addHashtag(name);
                publicationService.addHashtagTo(p, h);
            }
        }

        // Send a notification to all the followers of the user
        Set<User> followers = u.getFollowers();
        NotificationRequest request = new NotificationRequest();
        request.title = "New publication from " + u.getName();
        request.body = u.getName() + "uploaded a new publication, go see it!";
        for(User follower: followers){
            if(follower.getToken() != null){
                request.target = follower.getToken();
                NotificationService.getInstance().sendNotification(request);
            }else{
                System.out.println("Can't send notification, token is null!");
            }
        }

        return p.getId();
    }

    private List<String> searchForHashtags(String comment) {
        String[] words = comment.trim().split("\\s+"); // Split the comment into the different words, separated by spaces.
        List<String> hashtags = new ArrayList<>();
        for(String w : words){
            if(w.charAt(0) == '#'){ // If the split result starts with a #, then we add it to the list.
                w = w.substring(1); // Erase the # at the beginning of the word.
                hashtags.add(w);
            }
        }
        return hashtags;
    }

    @DeleteMapping(path="/{id}")
    public String deletePublication(HttpSession session, @PathVariable("id") Long publicationId) {

        publicationService.crud().deleteById(publicationId);
        return BaseController.OK_MESSAGE;
    }

    @DeleteMapping(path="/{id}/delLike")
    public Publication deleteLike(HttpSession session, @PathVariable("id") Long publicationId){
        Long userId = this.getLoggedUser(session);
        return publicationService.deleteLike(userId, publicationId);
    }

    @DeleteMapping(path="/{publicationId}/delComment/{commentId}")
    public String deleteComment(HttpSession session,
                                @PathVariable("publicationId") Long publicationId,
                                @PathVariable("commentId") Long commentId){
        commentService.crud().deleteById(commentId);
        return BaseController.OK_MESSAGE;
    }


    @PutMapping(path="/{publicationId}/editComment/{commentId}")
    public Comment editComment (HttpSession session,
                                @PathVariable("publicationId") Long publicationId,
                                @PathVariable("commentId") Long commentId,
                                @Valid @RequestBody  CommentPost cp){
        return commentService.editComment(commentId,cp.text);
    }


    static class PublicationPost {
        @NotNull
        public String photo;
        @NotNull
        public String description;
        @NotNull
        public Date date;
    }

    static class CommentPost{
        @NotNull
        public String text;
        @NotNull
        public Long publicationId;
    }



}

