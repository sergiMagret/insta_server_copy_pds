package org.udg.pds.springtodo.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.udg.pds.springtodo.entity.Comment;
import org.udg.pds.springtodo.entity.Publication;
import org.udg.pds.springtodo.entity.User;
import org.udg.pds.springtodo.entity.Views;
import org.udg.pds.springtodo.service.CommentService;
import org.udg.pds.springtodo.service.PublicationService;
import org.udg.pds.springtodo.service.UserService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.util.Date;

@RequestMapping(path="/comments")
@RestController
public class CommentController  extends BaseController {


}
