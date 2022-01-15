import { Component, OnInit } from '@angular/core';
import {Router} from "@angular/router";
import {NgForm} from "@angular/forms";

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

  constructor(private router: Router) { }

  ngOnInit(): void {
  }

  onSubmit(form: NgForm) {
    this.router.navigate(["search", form.value.search])
  }

  onClickEvent(value: string) {
    this.router.navigate([value])
  }

}
