package org.udg.pds.springtodo;

import io.minio.MinioClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.udg.pds.springtodo.entity.*;
import org.udg.pds.springtodo.service.*;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

@Service
public class Global {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    private MinioClient minioClient;

    private Logger logger = LoggerFactory.getLogger(Global.class);

    @Autowired
    private
    UserService userService;

    @Autowired
    private
    TaskService taskService;

    @Autowired
    private
    TagService tagService;

    @Autowired
    private
    PublicationService publicationService;

    @Autowired
    private
    CommentService commentService;

    @Value("${todospring.minio.url:http://94.130.183.121:9000}")
    private String minioURL;

    @Value("${todospring.minio.access-key:QNWFR36H1VJC44DE9QD5}")
    private String minioAccessKey;

    @Value("${todospring.minio.secret-key:psH0KyTV2JN49dmnOVl1xYDIsrqYy0caJStEL29s}")
    private String minioSecretKey;

    @Value("${todospring.minio.bucket:pds2b}")
    private String minioBucket;

    @Value("${todospring.base-url:#{null}}")
    private String BASE_URL;

    @Value("${todospring.base-port:8080}")
    private String BASE_PORT;



    @PostConstruct
    void init() {

        logger.info(String.format("Starting Minio connection to URL: %s", minioURL));
        try {
            minioClient = new MinioClient(minioURL, minioAccessKey, minioSecretKey);
        } catch (Exception e) {
            logger.warn("Cannot initialize minio service with url:" + minioURL + ", access-key:" + minioAccessKey + ", secret-key:" + minioSecretKey);
        }

        if (minioBucket == "") {
            logger.warn("Cannot initialize minio bucket: " + minioBucket);
            minioClient = null;
        }

        if (BASE_URL == null) BASE_URL = "http://localhost";
        BASE_URL += ":" + BASE_PORT;

        initData();
    }

    private void initData() {
        logger.info("Starting populating database ...");
        String desc = "Hi! I'm the default user and this is my description.";
        String profPic = "https://cdn.business2community.com/wp-content/uploads/2017/08/blank-profile-picture-973460_640.png";
        User user = userService.register("usuari", "Usuari Per Defecte", "usuari@hotmail.com", "123456", desc, profPic);

        /* Adding three publications associated with the user with id=1. */
        String publicationURL = "https://iso.500px.com/wp-content/uploads/2014/07/big-one.jpg";
        Publication p = new Publication(publicationURL, "this is the first description", new Date(2020, Calendar.JULY, 23));
        p.setUser(user);
        user.addPublication(p);
        publicationService.addPublication(p);

        publicationURL = "https://image.shutterstock.com/image-photo/colorful-hot-air-balloons-flying-260nw-1033306540.jpg";p = new Publication(publicationURL, "this is the second description", new Date(2020, Calendar.APRIL, 28));
        p.setUser(user);
        user.addPublication(p);
        publicationService.addPublication(p);

        publicationURL = "https://iso.500px.com/wp-content/uploads/2014/06/W4A2827-1-3000x2000.jpg";p = new Publication(publicationURL, "this is the third description!", new Date(2020, Calendar.APRIL, 28));
        p.setUser(user);
        user.addPublication(p);
        publicationService.addPublication(p);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 1988);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        Comment c = new Comment("Que guaiii");
        c.setUser(user);
        c.setPublication(p);
        c.setDate(cal.getTime());
        commentService.addComment(c);



        IdObject taskId = taskService.addTask("Una tasca", user.getId(), new Date(), new Date());
        Tag tag = tagService.addTag("ATag", "Just a tag");
        taskService.addTagsToTask(user.getId(), taskId.getId(), new ArrayList<Long>() {{
            add(tag.getId());
        }});

        profPic = "https://www.dailymoss.com/wp-content/uploads/2019/08/funny-profile-pic59.jpg";
        desc = "Hi! I'm another user and this is my description.\nI like things like music :)";
        user = userService.register("user2", "User By Default", "user2@hotmail.com", "0000", desc, profPic);

        /* Adding two publications associated with the user with id=2. */
        publicationURL = "https://petapixel.com/assets/uploads/2019/06/manipulatedelephant-800x534.jpg";
        p = new Publication(publicationURL, "this is an elephant!!", new Date(2020, Calendar.APRIL, 28));
        p.setUser(user);
        user.addPublication(p);
        publicationService.addPublication(p);

        publicationURL = "https://i.redd.it/g1ot0484k6821.png";
        p = new Publication(publicationURL, "this is a funny meme i found today on the internet", new Date(2020, Calendar.APRIL, 28));
        p.setUser(user);
        user.addPublication(p);
        publicationService.addPublication(p);

        profPic = "https://www.dailymoss.com/wp-content/uploads/2019/08/funny-profile-pic59.jpg";
        desc = "Hi! I'm another user and this is my description.\nI like things like music :)";
        user = userService.register("Maria", "User By Default", "Maria@hotmail.com", "0000", desc, profPic);

        c = new Comment("Ooohh");
        c.setUser(user);
        cal.set(Calendar.DAY_OF_MONTH, 2);
        c.setDate(cal.getTime());
        c.setPublication(p);

        commentService.addComment(c);

        profPic = "https://www.dailymoss.com/wp-content/uploads/2019/08/funny-profile-pic59.jpg";
        desc = "Hi! I'm another user and this is my description.\nI like things like music :)";
        user = userService.register("Oliu", "User By Default", "Oliu@hotmail.com", "0000", desc, profPic);

        c = new Comment("On es aixo??");
        c.setUser(user);
        cal.set(Calendar.DAY_OF_MONTH, 3);
        c.setDate(cal.getTime());
        c.setPublication(p);

        commentService.addComment(c);

        profPic = "https://www.dailymoss.com/wp-content/uploads/2019/08/funny-profile-pic59.jpg";
        desc = "Hi! I'm another user and this is my description.\nI like things like music :)";
        user = userService.register("Pau", "User By Default", "pau@hotmail.com", "0000", desc, profPic);

        c = new Comment("Australiaa??");
        c.setUser(user);
        cal.set(Calendar.DAY_OF_MONTH, 4);
        c.setDate(cal.getTime());
        c.setPublication(p);

        commentService.addComment(c);

        profPic = "https://www.dailymoss.com/wp-content/uploads/2019/08/funny-profile-pic59.jpg";
        desc = "Hi! I'm another user and this is my description.\nI like things like music :)";
        user = userService.register("Sergi", "User By Default", "sergi@hotmail.com", "0000", desc, profPic);

        c = new Comment("Buaaa que fas a australia?");
        c.setUser(user);
        cal.set(Calendar.DAY_OF_MONTH, 5);
        c.setDate(cal.getTime());
        c.setPublication(p);

        commentService.addComment(c);

        profPic = "https://www.dailymoss.com/wp-content/uploads/2019/08/funny-profile-pic59.jpg";
        desc = "Hi! I'm another user and this is my description.\nI like things like music :)";
        user = userService.register("Roger", "User By Default", "roger@hotmail.com", "0000", desc, profPic);

        c = new Comment("Jo tambe soc a australia! A quina ciutat estas? Jo estic a Sydney... ");
        c.setUser(user);
        cal.set(Calendar.DAY_OF_MONTH, 6);
        c.setDate(cal.getTime());
        c.setPublication(p);

        commentService.addComment(c);


        profPic = "https://www.dailymoss.com/wp-content/uploads/2019/08/funny-profile-pic59.jpg";
        desc = "Hi! I'm another user and this is my description.\nI like things like music :)";
        user = userService.register("Ainhoa", "User By Default", "ainhoa@hotmail.com", "0000", desc, profPic);
        c = new Comment("Soc moolt FAN ");
        c.setUser(user);
        cal.set(Calendar.DAY_OF_MONTH, 7);
        c.setDate(cal.getTime());
        c.setPublication(p);

        commentService.addComment(c);
        user = userService.register("Marc", "User By Default", "marc@hotmail.com", "0000", desc, profPic);
        c = new Comment("oohh");
        c.setUser(user);
        cal.set(Calendar.DAY_OF_MONTH, 8);
        c.setDate(cal.getTime());
        c.setPublication(p);

        commentService.addComment(c);
        user = userService.register("julia", "User By Default", "julia@hotmail.com", "0000", desc, profPic);
        c = new Comment("Quant tornes??");
        c.setUser(user);
        cal.set(Calendar.DAY_OF_MONTH, 9);
        c.setDate(cal.getTime());
        c.setPublication(p);

        commentService.addComment(c);
        user = userService.register("nil", "User By Default", "nil@hotmail.com", "0000", desc, profPic);
        c = new Comment("oohh ");
        c.setUser(user);
        cal.set(Calendar.DAY_OF_MONTH, 10);
        c.setDate(cal.getTime());
        c.setPublication(p);

        commentService.addComment(c);
        user = userService.register("lucia", "User By Default", "lucia@hotmail.com", "0000", desc, profPic);
        c = new Comment("Jo tambe soc a australia! ");
        c.setUser(user);
        cal.set(Calendar.DAY_OF_MONTH, 11);
        c.setDate(cal.getTime());
        c.setPublication(p);
        commentService.addComment(c);

        user = userService.register("marti", "User By Default", "marti@hotmail.com", "0000", desc, profPic);
        c = new Comment("A quina ciutat estas?");
        c.setUser(user);
        cal.set(Calendar.DAY_OF_MONTH, 12);
        c.setDate(cal.getTime());
        c.setPublication(p);
        commentService.addComment(c);

        user = userService.register("arnau", "arnau", "arnau@hotmail.com", "0000", desc, profPic);
        c = new Comment("WWWOOOOOWWWW ");
        c.setUser(user);
        cal.set(Calendar.DAY_OF_MONTH, 13);
        c.setDate(cal.getTime());
        c.setPublication(p);
        commentService.addComment(c);
        user = userService.register("paula", "paula", "paula@hotmail.com", "0000", desc, profPic);
        c = new Comment("Jo tambe soc a australia!!!");
        c.setUser(user);
        cal.set(Calendar.DAY_OF_MONTH, 14);
        c.setDate(cal.getTime());
        c.setPublication(p);
        commentService.addComment(c);
        user = userService.register("Abril", "Abril", "abril@hotmail.com", "0000", desc, profPic);
        c = new Comment("Jo tambe soc a australia! A quina ciutat estas? Jo estic a Sydney... ");
        c.setUser(user);
        cal.set(Calendar.DAY_OF_MONTH, 15);
        c.setDate(cal.getTime());
        c.setPublication(p);
        commentService.addComment(c);
        user = userService.register("Hugo", "Hugo", "hugo@hotmail.com", "0000", desc, profPic);
        c = new Comment("A quina ciutat estas? Jo estic a Sydney... ");
        c.setUser(user);
        cal.set(Calendar.DAY_OF_MONTH, 16);
        c.setDate(cal.getTime());
        c.setPublication(p);
        commentService.addComment(c);
        user = userService.register("Biel", "Biel", "biel@hotmail.com", "0000", desc, profPic);
        c = new Comment("Jo tambe soc a australia! A quina ciutat estas? Jo estic a Sydney... ");
        c.setUser(user);
        cal.set(Calendar.DAY_OF_MONTH, 17);
        c.setDate(cal.getTime());
        c.setPublication(p);
        commentService.addComment(c);

        userService.register("2Marc", "User By Default", "mar2c@hotmail.com", "0000", desc, profPic);
        userService.register("2julia", "User By Default", "jul2ia@hotmail.com", "0000", desc, profPic);
        userService.register("2nil", "User By Default", "nil@ho2tmail.com", "0000", desc, profPic);
        userService.register("2lucia", "User By Default", "lucia2@hotmail.com", "0000", desc, profPic);
        userService.register("2marti", "User By Default", "marti@2hotmail.com", "0000", desc, profPic);
        userService.register("2arnau", "arnau", "arnau@hotmail.com2", "0000", desc, profPic);
        userService.register("2paula", "paula", "paula@hotm2ail.com", "0000", desc, profPic);
        userService.register("2Abril", "Abril", "abril@hotma2il.com", "0000", desc, profPic);
        userService.register("2Hugo", "Hugo", "hugo@hotmail.c2om", "0000", desc, profPic);
        userService.register("2Biel", "Biel", "biel@hotmail.co2m", "0000", desc, profPic);

        userService.register("3Marc", "User By Default", "3marc@hotmail.com", "0000", desc, profPic);
        userService.register("3julia", "User By Default", "3julia@hotmail.com", "0000", desc, profPic);
        userService.register("3nil", "User By Default", "3nil@hotmail.com", "0000", desc, profPic);
        userService.register("3lucia", "User By Default", "3lucia@hotmail.com", "0000", desc, profPic);
        userService.register("3marti", "User By Default", "3marti@hotmail.com", "0000", desc, profPic);
        userService.register("3arnau", "arnau", "3arnau@hotmail.com", "0000", desc, profPic);
        userService.register("3paula", "paula", "3paula@hotmail.com", "0000", desc, profPic);
        userService.register("3Abril", "Abril", "3abril@hotmail.com", "0000", desc, profPic);
        userService.register("3Hugo", "Hugo", "3hugo@hotmail.com", "0000", desc, profPic);
        userService.register("3Biel", "Biel", "3biel@hotmail.com", "0000", desc, profPic);

        userService.register("4Marc", "User By Default", "4marc@hotmail.com", "0000", desc, profPic);
        userService.register("4julia", "User By Default", "4julia@hotmail.com", "0000", desc, profPic);
        userService.register("4nil", "User By Default", "4nil@hotmail.com", "0000", desc, profPic);
        userService.register("4lucia", "User By Default", "4lucia@hotmail.com", "0000", desc, profPic);
        userService.register("4marti", "User By Default", "4marti@hotmail.com", "0000", desc, profPic);
        userService.register("4arnau", "arnau", "4arnau@hotmail.com", "0000", desc, profPic);
        userService.register("4paula", "paula", "4paula@hotmail.com", "0000", desc, profPic);
        userService.register("4Abril", "Abril", "4abril@hotmail.com", "0000", desc, profPic);
        userService.register("4Hugo", "Hugo", "4hugo@hotmail.com", "0000", desc, profPic);
        userService.register("4Biel", "Biel", "4biel@hotmail.com", "0000", desc, profPic);

        userService.register("5Marc", "User By Default", "5marc@hotmail.com", "0000", desc, profPic);
        userService.register("5julia", "User By Default", "5julia@hotmail.com", "0000", desc, profPic);
        userService.register("5nil", "User By Default", "5nil@hotmail.com", "0000", desc, profPic);
        userService.register("5lucia", "User By Default", "5lucia@hotmail.com", "0000", desc, profPic);
        userService.register("5marti", "User By Default", "5marti@hotmail.com", "0000", desc, profPic);
        userService.register("5arnau", "arnau", "5arnau@hotmail.com", "0000", desc, profPic);
        userService.register("5paula", "paula", "5paula@hotmail.com", "0000", desc, profPic);
        userService.register("5Abril", "Abril", "5abril@hotmail.com", "0000", desc, profPic);
        userService.register("5Hugo", "Hugo", "5hugo@hotmail.com", "0000", desc, profPic);
        userService.register("5Biel", "Biel", "5biel@hotmail.com", "0000", desc, profPic);


    }

    public MinioClient getMinioClient() {
        return minioClient;
    }

    public String getMinioBucket() {
        return minioBucket;
    }

    public String getBaseURL() {
        return BASE_URL;
    }
}
