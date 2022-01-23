import { Component, OnInit } from '@angular/core';
import {ForumService} from "../../services/forum.service";
import {ActivatedRoute, Router} from "@angular/router";
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'app-createpost',
  templateUrl: './create-post.component.html',
  styleUrls: ['./create-post.component.css']
})
export class CreatePostComponent implements OnInit {

  post: FormGroup = this.initForm();

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private service: ForumService,
    private formBuilder: FormBuilder
  ) { }

  ngOnInit(): void {
  }

  initForm() {
    return this.formBuilder.group({
      title: new FormControl('',
        [Validators.required, Validators.maxLength(60)]
      ),
      msg: new FormControl('',
        [Validators.required, Validators.maxLength(2000)]
      ),
      id: 0,
      votes: 0,
      author: "",
    });
  }

  hasError(path: string, errorCode: string) {
    return this.post && this.post.hasError(errorCode, path);
  }

  create() {
    const post = { ...this.post.value}
    this.service.post(post).subscribe(
      result => {},
      error => {},
      () => {
        this.router.navigate(["forum"]).then()
      }
    );
  }
}
