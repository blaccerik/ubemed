import { Component, OnInit } from '@angular/core';
import {Router} from "@angular/router";
import {NgForm} from "@angular/forms";
import {MatDialog} from "@angular/material/dialog";
import {LoginComponent} from "../login/login.component";
import {AuthService} from "../services/auth.service";
import {InventoryService} from "../services/inventory.service";

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

  coins: number;
  lastClaimDate: number;

  constructor(
    private router: Router,
    private dialog: MatDialog,
    public service: AuthService
  ) { }

  ngOnInit(): void {
    this.update()
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

  update() {
    this.service.getData(true).subscribe(next => {
      this.coins = next.coins;
      this.lastClaimDate = next.lastClaimDate;
    })
  }

  logout() {
    this.service.logout();
  }

  canClaim(): boolean {
    const date = new Date();
    if (!isNaN(this.lastClaimDate)) {
      const diffTime = date.getTime() - this.lastClaimDate
      const diff = Math.ceil(diffTime / (1000 * 60 * 60));
      return diff >= 24;
    }
    return false;
  }

  claim() {
    this.service.claim().subscribe(
      next => {
        this.update();
      }
    )
  }
}
