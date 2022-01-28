import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {ForumService} from "../../services/forum.service";
import {Post} from "../../model/Post";
import {AuthService} from "../../services/auth.service";
import {Vote} from "../../model/Vote";

@Component({
  selector: 'app-post',
  templateUrl: './post.component.html',
  styleUrls: ['./post.component.css']
})
export class PostComponent implements OnInit {

  post: Post;

  constructor(
    private service: ForumService,
    private route: ActivatedRoute,
    private router: Router,
    public authService: AuthService,
  ) { }

  ngOnInit(): void {
    let id = this.route.snapshot.paramMap.get('id');
    if (id !== null) {
      this.service.get(id).subscribe(
        (data) => {
          console.log(data)
          if (data) {
            hideloader();
          } else {
            this.router.navigate(["error"])
          }
          this.post = data;
          },
        () => {
          this.router.navigate(["error"])
        }
      );
    }

    function hideloader() {
      // @ts-ignore
      document.getElementById('loading').style.display = "none";
    }
  }

  vote(event: Event, upvote : boolean, id: number, vote: string) {
    event.stopPropagation();
    this.service.put(new Vote(upvote), id).subscribe();
    this.changePostVote(this.post, upvote, vote);

  }

  changePostVote(post: Post, upvote: boolean, vote: string) {
    if ((upvote && vote == "upvote") || (!upvote && vote == "downvote")) {
      post.myVote = "neutral";
      if (upvote) {
        post.votes = post.votes - 1;
      } else {
        post.votes = post.votes + 1;
      }
    } else if (upvote) {
      if (post.myVote == "downvote") {
        post.votes = post.votes + 2
      } else {
        post.votes = post.votes + 1
      }
      post.myVote = "upvote";
    } else {
      if (post.myVote == "upvote") {
        post.votes = post.votes - 2
      } else {
        post.votes = post.votes - 1
      }
      post.myVote = "downvote";
    }
  }

}
