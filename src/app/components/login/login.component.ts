import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {ForumService} from "../services/forum.service";
import {AuthService} from "../services/auth.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  login: FormGroup = this.initForm();

  constructor(
    // private router: Router,
    // private route: ActivatedRoute,
    public service: AuthService,
    private formBuilder: FormBuilder
  ) {}

  ngOnInit() {
    this.login = this.initForm()
  }

  initForm() {
    return this.formBuilder.group({
      username: new FormControl('',
        [Validators.required]
      ),
      password: new FormControl('',
        [Validators.required]
      ),
    });
  }

  hasError(path: string, errorCode: string) {
    return this.login && this.login.hasError(errorCode, path);
  }

  submit() {
    const login = { ...this.login.value};
    this.service.post(login).subscribe(
      result => {
        console.log(result);
      },
      error => {},
      () => {}
    );
    // this.clickMethod();
  }

}
