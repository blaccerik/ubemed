import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {AuthService} from "../../services/auth.service";
import {Router} from "@angular/router";
import {TranslateService} from "@ngx-translate/core";
import {StoreService} from "../../services/store.service";
import {Product} from "../../model/Product";

@Component({
  selector: 'app-store-new',
  templateUrl: './store-new.component.html',
  styleUrls: ['./store-new.component.css']
})
export class StoreNewComponent implements OnInit {

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private storeService: StoreService,
    private router: Router
  ) { }

  categories = [
    { name: 'store.new.cat1', selected: false },
    { name: 'store.new.cat2', selected: false },
    { name: 'store.new.cat3', selected: false },
    { name: 'store.new.cat4', selected: false },
    { name: 'store.new.cat5', selected: false },
    { name: 'store.new.cat6', selected: false },
  ];

  shop: FormGroup;
  max = 100;
  title = 50;
  filePath: string;
  text: string;

  ngOnInit(): void {
    if (this.authService.isLoggedIn()) {
      this.router.navigate(["store"]).then();
    }
    this.shop = new FormGroup({
      cats: new FormControl([], [Validators.required, Validators.maxLength(3)]),
      title: new FormControl("", [Validators.required, Validators.maxLength(this.title)]),
      cost: new FormControl("", [Validators.min(1), Validators.max(this.max), Validators.required]),
      img: new FormControl("", [Validators.required]),
    });
  }

  imagePreview(e: any) {
    // @ts-ignore
    const file = (e.target as HTMLInputElement).files[0];

    this.shop.patchValue({
      img: file
    });
    // @ts-ignore
    this.shop.get('img').updateValueAndValidity()
    if (file) {
      const reader = new FileReader();
      reader.onload = () => {
        this.filePath = reader.result as string;
      }
      reader.readAsDataURL(file)
    }
  }


  hasError(path: string, errorCode: string) {
    return this.shop && this.shop.hasError(errorCode, path);
  }

  submit() {
    const listing: Product = { ...this.shop.value}
    this.storeService.upload(listing).subscribe(
      next => {
        console.log(next);
      }

    )
    console.log(listing)
  }

  changeSelected($event: any, category: any): void {
    category.selected = $event.selected;
    let list = []
    for (let i = 0; i < this.categories.length; i++) {
      let value = this.categories[i];
      if (value.selected) {
        list.push(value.name)
      }
    }
    this.shop.setControl("cats",
      new FormControl(list, [Validators.required, Validators.maxLength(3)])
    )
  }

}
