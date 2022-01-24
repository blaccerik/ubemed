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
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {Login} from "../model/Login";


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
  success: boolean = true;
  wrongPass: boolean = false;

  constructor(
    // private router: Router,
    // private route: ActivatedRoute,
    public service: AuthService,
    private formBuilder: FormBuilder,
    private dialogref: MatDialogRef<LoginComponent>
  ) {

  }

  ngOnInit() {
    // this.login = this.initFormLogin()
    // this.register = this.initFormRegister();
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
      // todo proper validator for pass2
      password2: new FormControl('', [Validators.required]),
    });
  }


  match(form: FormGroup) {
    const pass1 = form.get("password")?.value;
    const pass2 = form.get("password2")?.value;
    return pass1 === pass2
  }

  hasError(path: string, errorCode: string) {
    return this.login && this.login.hasError(errorCode, path);
  }

  submitLogin() {
    const login = { ...this.login.value};
    this.service.login(login).subscribe(
      result => {
        this.wrongPass = false;
        this.dialogref.close();
      },
      error => {
        this.wrongPass = true;
      }
    );
    // this.clickMethod();
  }

  submitRegister() {
    const register = { username: this.register.value.username, password: this.register.value.password};
    this.service.register(register).subscribe(
      result => {
        if (result) {
          this.success = true;
          this.showLogIn = true;
        } else {
          this.success = false;
        }
      }
    );
    // this.clickMethod();
  }

  onClickEvent(value: string) {
    // this.router.navigate([value])
  }

}
