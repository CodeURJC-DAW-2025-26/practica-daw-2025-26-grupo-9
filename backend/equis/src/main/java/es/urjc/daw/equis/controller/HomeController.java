package es.urjc.daw.equis.controller;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.urjc.daw.equis.model.Category;
import es.urjc.daw.equis.model.Comment;
import es.urjc.daw.equis.model.Post;
import es.urjc.daw.equis.model.User;
import es.urjc.daw.equis.service.CategoryService;
import es.urjc.daw.equis.service.LikeService;
import es.urjc.daw.equis.service.PostService;
import es.urjc.daw.equis.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class HomeController {


    @Autowired
    private final PostService postService;

    @Autowired
    private final UserService userService;

    @Autowired
    private final CategoryService categoryService;

    @Autowired
    private final LikeService likeService;



    public HomeController(CategoryService categoryService, LikeService likeService, PostService postService, UserService userService){
        this.postService = postService;
        this.userService = userService;
        this.categoryService = categoryService;
        this.likeService = likeService;
    }

    @GetMapping("/")
    public String home(Model model,
            @RequestParam(name = "page", defaultValue = "1") int page,
            HttpServletRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        int size = 10;
        if (page < 1)
            page = 1;

        List<Post> allPosts = postService.findAllPosts();
        List<Post> ordered = applyAlgorithm(allPosts);

        User currentUser = null;
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            currentUser = userService.findByEmail(auth.getName()).orElse(null);
        }

        for (Post post : ordered) {
            if (post.getComments() != null) {
                for (Comment comment : post.getComments()) {
                    boolean isOwner = comment.getUser() != null
                            && currentUser != null
                            && comment.getUser().getId().equals(currentUser.getId());
                    comment.setOwner(isOwner);
                }
            }
        }

        Page<Post> pageResult = sliceAsPage(ordered, page, size);

        model.addAttribute("posts", pageResult.getContent());
        model.addAttribute("nextPage", page + 1);
        model.addAttribute("hasNext", pageResult.hasNext());
        model.addAttribute("currentUser", currentUser);

        List<Category> cats = categoryService.findAll();
        cats.forEach(c -> c.setPostsCount(postService.countByCategoryId(c.getId())));

        model.addAttribute("categories", cats);
        model.addAttribute("topCategories", topCategories(5));

        String currentPath = request.getRequestURI()
                + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        model.addAttribute("currentPath", currentPath);

        return "index";
    }

    @GetMapping("/stats")
    public String stats(Model model) throws Exception {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean loggedIn = auth != null
                && auth.isAuthenticated()
                && !"anonymousUser".equals(auth.getName());

        if (!loggedIn) {
            return "redirect:/login";
        }

        List<Post> allPosts = postService.findAllPosts();
        List<Post> ordered = applyAlgorithm(allPosts);
        List<Post> top = ordered.stream().limit(5).toList();

        List<String> labels = top.stream()
                .map(post -> post.getContent().replaceAll("\\?+$", ""))
                .toList();

        List<Long> likes = top.stream()
                .map(Post::getLikesCount)
                .toList();

        ObjectMapper mapper = new ObjectMapper();

        model.addAttribute("labels", mapper.writeValueAsString(labels));
        model.addAttribute("likes", mapper.writeValueAsString(likes));
        model.addAttribute("currentUser", userService.findByEmail(auth.getName()).orElse(null));
        model.addAttribute("statsActive", true);

        return "stats";
    }

    private List<Post> applyAlgorithm(List<Post> posts) {

        List<Post> copy = new ArrayList<>(posts);

        copy.forEach(post -> {

            long realLikes = likeService.countByPost(post);
            post.setLikesCount(realLikes);

            if (post.getComments() != null) {
                post.getComments().forEach(comment -> {
                    long commentLikes = likeService.countByComment(comment);
                    comment.setLikesCount(commentLikes);
                });
            }
        });

        copy.sort((p1, p2) -> {
            long score1 = calculateScore(p1);
            long score2 = calculateScore(p2);
            return Long.compare(score2, score1);
        });

        return copy;
    }

    private long calculateScore(Post post) {

        long likes = likeService.countByPost(post);
        long comments = post.getComments() != null ? post.getComments().size() : 0;

        long hoursSincePost = 0;
        if (post.getDate() != null) {
            hoursSincePost = ChronoUnit.HOURS
                    .between(post.getDate(), LocalDateTime.now());
        }

        long freshnessScore = Math.max(0, 1000 - hoursSincePost);

        return (likes * 3) + (comments * 2) + freshnessScore;
    }

    private Page<Post> sliceAsPage(List<Post> ordered, int page1Based, int size) {
        int from = (page1Based - 1) * size;
        int to = Math.min(from + size, ordered.size());
        List<Post> content = from >= ordered.size() ? List.of() : ordered.subList(from, to);

        return new PageImpl<>(
                content,
                PageRequest.of(Math.max(page1Based - 1, 0), size),
                ordered.size());
    }

    private List<Category> topCategories(int limit) {
        List<Category> cats = categoryService.findAll();
        cats.forEach(c -> c.setPostsCount(postService.countByCategoryId(c.getId())));
        cats.sort((c1, c2) -> Long.compare(c2.getPostsCount(), c1.getPostsCount()));
        return cats.stream().limit(limit).toList();
    }
}