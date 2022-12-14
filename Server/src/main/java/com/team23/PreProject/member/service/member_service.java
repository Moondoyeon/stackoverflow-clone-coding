package com.team23.PreProject.member.service;

import com.team23.PreProject.answer.entity.answer;
import com.team23.PreProject.answer.repository.answer_repository;
import com.team23.PreProject.comment.entity.comment;
import com.team23.PreProject.comment.repository.comment_repository;
import com.team23.PreProject.member.dto.member_create_dto;
import com.team23.PreProject.member.entity.member;
import com.team23.PreProject.member.repository.member_repository;
import com.team23.PreProject.member_post.entitiy.member_post;
import com.team23.PreProject.member_post.repository.member_post_repository;
import com.team23.PreProject.post.entity.post;
import com.team23.PreProject.post.repository.post_repository;
import com.team23.PreProject.post_vote.entity.post_vote;
import com.team23.PreProject.post_vote.repository.post_vote_repository;
import com.team23.PreProject.profile.entity.profile;
import com.team23.PreProject.profile.repository.profile_repository;
import com.team23.PreProject.token.login;
import com.team23.PreProject.token.login_repository;
import com.team23.PreProject.token.logout;
import com.team23.PreProject.token.logout_repository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service

public class member_service {
    @Autowired
    member_repository member_repository;
    @Autowired
    profile_repository profile_repository;
    @Autowired

    post_repository post_repository;

    @Autowired
    post_vote_repository post_vote_repository;
    @Autowired
    member_post_repository member_post_repository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    com.team23.PreProject.token.logout_repository logout_repository;

    @Autowired
    login_repository  login_repository;

    @Autowired
    answer_repository answer_repository;

    @Autowired
    com.team23.PreProject.comment.repository.comment_repository comment_repository;

    public member insert_member(member_create_dto dto){
        member member = new member(dto.getPassword(),dto.getNickName(),dto.getId());
        member.setPassword(bCryptPasswordEncoder.encode(member.getPassword()));
        profile profile = new profile();
        //?????? ??????
        member = member_repository.save(member);
        //????????? ??????
        //profile.setMember(member);
        profile.setDisplayname(member.getNickName());
        profile = profile_repository.save(profile);
        //?????? ??????
        member.setProfile(profile);
        member.setRoles("ROLE_USER");
        member.setProfileImage("default.png");
        //?????? ????????????
        member_repository.save(member);




        return member;

    }

    public member findMemberById(Integer member_id){
        try {
            member member = member_repository.findById(member_id).get();
            return member;
        }catch (NoSuchElementException e)
        {
            return null;
        }


    }//findMemberById end

    public String updatePassword(Integer member_id, String elder, String newer ){
        member member = member_repository.findById(member_id).get();
        BCryptPasswordEncoder encoder = bCryptPasswordEncoder;
        if(encoder.matches(elder,member.getPassword())&&!newer.equals("")){
            member.setPassword(encoder.encode(newer));
            member_repository.flush();
            return "passwored changed";
        }
        else
            return "passwored not changed";

    }//updatePassword


    public String deleteMember(Integer member_id) {
        try {
            if (member_id == 1)
                return "delete fail";
            member member = member_repository.findById(member_id).get();

            member deleted = member_repository.findById(1).get();
            //post vote -> delted ??????
            List<post_vote> post_votes;
            post_votes = member.getPost_votes();
            List<answer> answers = member.getAnswers();
            List<comment> comments = comment_repository.findByMember(member);
            for (comment com : comments) {
                com.setMember(deleted);


            }
            comment_repository.flush();
            for (answer ans : answers) {
                ans.setMember(deleted);
                answer_repository.flush();
            }

            for (post_vote post_vote : post_votes) {
                post_vote.setMember(deleted);
                post_vote_repository.flush();
            }

            System.out.println("================================post vote flush()\n\n");

            List<member_post> member_posts = member.getMember_posts();
            member.setMember_posts(null);
            for (member_post member_post : member_posts) {

                member_post.setMember(deleted);
                member_post_repository.flush();

            }

            deleted.setMember_posts(member_posts);
            System.out.println("================================member_post flush()\n\n");


            member_repository.flush();
            member_repository.delete(member);


            try {
                member = member_repository.findById(member_id).get();
                return "delete fail";
            } catch (Exception e) {
                return "delete suc";
            }
        }catch(Exception e)
        {
            return "delete fail";
        }




    }

    public boolean checkExistId(String id) {
        member member =  member_repository.findByid(id);
        boolean result;
        if(member == null)
        {
            result = false;

        }
        else
            result = true;

        return result;
    }

    public boolean checkLogout(String token) {

        logout logout = logout_repository.findByToken(token);
        login login = login_repository.findByToken(token);

        if(logout != null) {
            System.out.println("=================== logout is not null \n\n");
            if(login ==null)//???????????? ?????? ?????? ????????? ?????? ????????? ???????????? ????????? ??????
                return true;
            else//????????? ?????? ????????? ?????? = ????????????
                return false;
        }
        else
        {
            System.out.println("=================== logout null \n\n");
            if(login!=null)//???????????? ?????? ??????, ????????? ????????? ?????? - ????????? ??????
                return false;
            return true;//???????????? ?????? ??????, ????????? ????????? ?????? - ????????? ??????
        }

        
    }

    public String logout(String token) {

        //?????? ????????? ???????????? ????????? ?????? ????????? ??????
        logout logout = logout_repository.findByToken(token);
        login login = login_repository.findByToken(token);
        if(logout == null)//???????????? ????????? ????????? ???????????? ???????????? ???????????? ???????????? ??????
        {
            if(login !=null)//???????????? ??????
            {
                logout newLogout = new logout();
                newLogout.setToken(token);
                logout_repository.save(newLogout); // ?????? ?????? ????????? logout ????????? ??????
                login_repository.delete(login);//?????? ????????? ????????? ?????? ??????
                return "true";
            }
            else
            {
                System.out.println("========================not logined token\n\n");
                return "false";
            }

        }
        else//???????????? ????????? ????????? ???????????? ?????? X
        {
            return "false";
        }


    }

    public void refresh(String token) {

        logout logout = logout_repository.findByToken(token);
        if(logout == null)//???????????? ????????? ?????? ????????? ????????? ????????? ????????? ????????? ????????? ??????, ??????????????? ?????? ??????
        {
            login login = login_repository.findByToken(token);
            if(login ==null) {
                login = new login();
                login.setToken(token);
                login_repository.save(login);
            }

        }
        else//???????????? ????????? ????????? ?????? ???????????? ????????? ??????
        {
            logout_repository.deleteById(logout.getId()); // ?????? ???????????? ????????? ???????????? ?????? ??????
        }
        
    }//refresh


}
