package org.udg.pds.springtodo;

import io.minio.MinioClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.udg.pds.springtodo.entity.IdObject;
import org.udg.pds.springtodo.entity.Publication;
import org.udg.pds.springtodo.entity.Tag;
import org.udg.pds.springtodo.entity.User;
import org.udg.pds.springtodo.service.PublicationService;
import org.udg.pds.springtodo.service.TagService;
import org.udg.pds.springtodo.service.TaskService;
import org.udg.pds.springtodo.service.UserService;

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

    @Value("${todospring.minio.url:}")
    private String minioURL;

    @Value("${todospring.minio.access-key:}")
    private String minioAccessKey;

    @Value("${todospring.minio.secret-key:}")
    private String minioSecretKey;

    @Value("${todospring.minio.bucket:}")
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

        publicationURL = "https://image.shutterstock.com/image-photo/colorful-hot-air-balloons-flying-260nw-1033306540.jpg";
        p = new Publication(publicationURL, "this is the second description", new Date(2020, Calendar.APRIL, 28));
        p.setUser(user);
        user.addPublication(p);
        publicationService.addPublication(p);

        publicationURL = "https://iso.500px.com/wp-content/uploads/2014/06/W4A2827-1-3000x2000.jpg";
        p = new Publication(publicationURL, "this is the third description!", new Date(2020, Calendar.APRIL, 28));
        p.setUser(user);
        user.addPublication(p);
        publicationService.addPublication(p);

        IdObject taskId = taskService.addTask("Una tasca", user.getId(), new Date(), new Date());
        Tag tag = tagService.addTag("ATag", "Just a tag");
        taskService.addTagsToTask(user.getId(), taskId.getId(), new ArrayList<Long>() {{
            add(tag.getId());
        }});

        profPic = "https://www.dailymoss.com/wp-content/uploads/2019/08/funny-profile-pic59.jpg";
        desc = "Hi! I'm another user and this is my description.\nI like things like music :)";
        user = userService.register("user", "User By Default", "user@hotmail.com", "0000", desc, profPic);

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
