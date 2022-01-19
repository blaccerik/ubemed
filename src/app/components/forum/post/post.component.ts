import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {ForumService} from "../../services/forum.service";
import {Post} from "../../model/Post";

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
  ) { }

  ngOnInit(): void {
    let id = this.route.snapshot.paramMap.get('id');
    if (id !== null) {
      this.service.get(id).subscribe(
        (data) => {
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
