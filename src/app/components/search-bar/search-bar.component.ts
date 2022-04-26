import { Component, OnInit } from '@angular/core';
import {Router} from "@angular/router";
import {NgForm} from "@angular/forms";
import {MatDialog} from "@angular/material/dialog";
import {LoginComponent} from "../login/login.component";
import {AuthService} from "../services/auth.service";

interface Event {
  value: string;
  viewValue: string;
}

@Component({
  selector: 'app-search-bar',
  templateUrl: './search-bar.component.html',
  styleUrls: ['./search-bar.component.scss']
})
export class SearchBarComponent implements OnInit {

  events: Event[] = [
    {value: 'suvepyks', viewValue: 'SuvePüks 2003'},
  ];

  constructor(
    private router: Router,
    private dialog: MatDialog,
    public service: AuthService
  ) { }

  ngOnInit(): void {
    this.service.update();
  }

  onSubmit(form: NgForm) {
    this.router.navigate(["search", form.value.search])
  }

  onClickEvent(value: string) {
    this.router.navigate([value])
  }

  myFunction() {

    // @ts-ignore
    document.getElementById("dropdown").classList.toggle("show");
  }


  openDialog() {
    this.dialog.open(LoginComponent);
  }

  logout() {
    this.service.logout();
  }

  canClaim(): boolean {
    const date = new Date();
    if (this.service.getLastClaimDate() != undefined) {
      const diffTime = date.getTime() - this.service.getLastClaimDate()
      const diff = Math.ceil(diffTime / (1000 * 60 * 60));
      return diff >= 24;
    }
    return false;
  }

  claim() {
    this.service.claim().subscribe(
      next => {
        this.service.update();
        // this.update();
      }
    )
  }
}
