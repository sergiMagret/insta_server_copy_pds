package org.udg.pds.springtodo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.udg.pds.springtodo.controller.exceptions.ServiceException;
import org.udg.pds.springtodo.entity.Hashtag;
import org.udg.pds.springtodo.entity.Publication;
import org.udg.pds.springtodo.repository.HashtagRepository;
import org.udg.pds.springtodo.repository.PublicationRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class HashtagService {
    @Autowired
    HashtagRepository hashtagRepository;

    @Autowired
    PublicationService publicationService;

    public HashtagRepository crud(){
        return hashtagRepository;
    }

    public Collection<Hashtag> getHashtags(){
        return hashtagRepository.getAll();
    }

    public Hashtag getHashtag(Long id){
        Optional<Hashtag> oh = hashtagRepository.findById(id);
        if(!oh.isPresent()){
            throw new ServiceException("Hashtag does not exist");
        }else{
            return oh.get();
        }
    }

    public Hashtag getHashtagByName(String name){
        List<Hashtag> hashtags = hashtagRepository.findByName(name);
        if(hashtags.size() == 0){
            throw new ServiceException("There's no hashtags with this name!");
        }else{
            return hashtags.get(0);
        }
    }

    public Hashtag addHashtag(String name){
        List<Hashtag> hashtags = hashtagRepository.findByName(name);
        if(hashtags.size() > 0)
            throw new ServiceException("The hashtag already exists!");
        else{
            Hashtag h = new Hashtag(name);
            hashtagRepository.save(h);
            return h;
        }
    }
}
