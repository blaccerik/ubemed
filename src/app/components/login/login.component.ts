import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  Validators
} from "@angular/forms";
import {AuthService} from "../services/auth.service";
import {MatDialog, MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  login: FormGroup = this.initFormLogin();
  register: FormGroup = this.initFormRegister();
  showLogIn: boolean = true;
  nameTaken: boolean = false;
  wrongPass: boolean = false;

  constructor(
    public service: AuthService,
    private formBuilder: FormBuilder,
    private dialogref: MatDialogRef<LoginComponent>
  ) {}

  ngOnInit() {}

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
    },{validator: this.checkIfMatchingPasswords('password', 'password2')}
    );
  }

  checkIfMatchingPasswords(passwordKey: string, passwordConfirmationKey: string) {
    return (group: FormGroup) => {
      let passwordInput = group.controls[passwordKey],
        passwordConfirmationInput = group.controls[passwordConfirmationKey];
      if (passwordInput.value !== passwordConfirmationInput.value) {
        return passwordConfirmationInput.setErrors({notEquivalent: true})
      }
      else {
        return passwordConfirmationInput.setErrors(null);
      }
    }
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
  }

  submitRegister() {
    const register = { username: this.register.value.username, password: this.register.value.password};
    console.log(register)
    this.service.register(register).subscribe(
      result => {
        console.log(result);
        if (result) {
          this.nameTaken = false;
          this.showLogIn = true;
        } else {
          this.nameTaken = true;
        }
      }
    );
  }
}
