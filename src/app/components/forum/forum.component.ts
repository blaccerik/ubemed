import { Component, OnInit } from '@angular/core';
import {Router} from "@angular/router";
import {ForumService} from "../services/forum.service";
import {Post} from "../model/Post";

@Component({
  selector: 'app-forum',
  templateUrl: './forum.component.html',
  styleUrls: ['./forum.component.css']
})
export class ForumComponent implements OnInit {

  posts: Post[] = [];

  constructor(
    private router: Router,
    private service: ForumService,
  ) { }

  ngOnInit(): void {
    this.service.getAll().subscribe(
      posts => {
        if (posts) {
          hideloader();
        }
        this.posts = posts});
    function hideloader() {
      // @ts-ignore
      document.getElementById('loading').style.display = "none";
    }
  }

  click(id: number) {
    this.router.navigate(["forum", id])
  }

  create() {
    this.router.navigate(["forum/new"])
  }

  vote(event: Event, upvote : boolean, id: number) {
    // todo add logic to vote
    event.stopPropagation();
    if (upvote) {
      console.log("bbb")
    } else {
      console.log("cc")
    }
  }

}
