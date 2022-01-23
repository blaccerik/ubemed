import { Component, OnInit } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormControl,
  FormGroup,
  ValidationErrors,
  ValidatorFn,
  Validators
} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {ForumService} from "../services/forum.service";
import {AuthService} from "../services/auth.service";
import {MatDialog} from "@angular/material/dialog";


interface Event {
  value: string;
  viewValue: string;
}

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  login: FormGroup = this.initFormLogin();
  register: FormGroup = this.initFormRegister();
  showLogIn: boolean = true;

  constructor(
    // private router: Router,
    // private route: ActivatedRoute,
    public service: AuthService,
    private formBuilder: FormBuilder,
  ) {

  }

  ngOnInit() {
    this.login = this.initFormLogin()
    this.register = this.initFormRegister();
  }

  initFormLogin() {
    return this.formBuilder.group({
      username: new FormControl('',
        [Validators.required]
      ),
      password: new FormControl('',
        [Validators.required]
      ),
    });
  }

  initFormRegister() {
    return  this.formBuilder.group({
      username: new FormControl('', [Validators.required]),
      password: new FormControl('', [Validators.required]),
      password2: new FormControl('', [Validators.required]),
    });
  }


  match() {
    const pass1 = this.register.get("password")?.value;
    const pass2 = this.register.get("password2")?.value;
    return pass1 !== pass2
  }

  hasError(path: string, errorCode: string) {
    return this.login && this.login.hasError(errorCode, path);
  }

  submitLogin() {
    const login = { ...this.login.value};
    this.service.login(login).subscribe(
      result => {
        console.log(result);
      },
      error => {},
      () => {}
    );
    // this.clickMethod();
  }

  submitRegister() {
    const register = { ...this.register.value};
    this.service.register(register).subscribe(
      result => {
        console.log(result);
      },
      error => {},
      () => {}
    );
    // this.clickMethod();
  }

  onClickEvent(value: string) {
    // this.router.navigate([value])
  }

}
