import { Component, OnInit } from '@angular/core';
import {Router} from "@angular/router";
import {ForumService} from "../services/forum.service";
import {Post} from "../model/Post";
import {Vote} from "../model/Vote";
import {AuthService} from "../services/auth.service";

@Component({
  selector: 'app-forum',
  templateUrl: './forum.component.html',
  styleUrls: ['./forum.component.css']
})
export class ForumComponent implements OnInit {
  public sort: string;
  posts: Post[] = [];

  constructor(
    private router: Router,
    private service: ForumService,
    public authService: AuthService
  ) { }

  ngOnInit(): void {
    this.service.getAll().subscribe(
      posts => {
        if (posts) {
          hideloader();
        }
        this.posts = posts
      });

    function hideloader() {
      // @ts-ignore
      document.getElementById('loading').style.display = "none";
    }
  }

  sortForum(sort: string) {
    if (sort == "votes") {
      this.posts.sort((a, b) => (a.votes < b.votes) ? 1 : -1)
    } else if (sort == "new") {
      this.posts.sort((a, b) => (a.id < b.id) ? 1 : -1)
    } else {
      this.posts.sort((a, b) => (a.id > b.id) ? 1 : -1)
    }
  }

  click(id: number) {
    this.router.navigate(["forum", id])
  }

  create() {
    this.router.navigate(["forum/new"])
  }

  vote(event: Event, upvote : boolean, id: number, vote: string) {
    event.stopPropagation();
    this.service.put(new Vote(upvote), id).subscribe();
    let post = this.posts.find(element => element.id == id);
    if (post) {
      this.changePostVote(post, upvote, vote);
    }
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
