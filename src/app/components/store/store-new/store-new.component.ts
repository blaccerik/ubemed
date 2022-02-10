import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {AuthService} from "../../services/auth.service";
import {Router} from "@angular/router";
import {TranslateService} from "@ngx-translate/core";
import {StoreService} from "../../services/store.service";
import {Product} from "../../model/Product";
import {importType} from "@angular/compiler/src/output/output_ast";

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
    { name: 'store.new.cat1', selected: false, value: '1'},
    { name: 'store.new.cat2', selected: false, value: '2'},
    { name: 'store.new.cat3', selected: false, value: '3'},
    { name: 'store.new.cat4', selected: false, value: '4'},
    { name: 'store.new.cat5', selected: false, value: '5'},
    { name: 'store.new.cat6', selected: false, value: '6'},
  ];

  shop: FormGroup;
  maxCost = 100;
  title = 50;
  filePath: string;
  text: string;
  showError: boolean
  imageMaxSize = 200;

  ngOnInit(): void {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(["store"]).then();
    }
    this.showError = false;
    this.shop = new FormGroup({
      cats: new FormControl([], [Validators.required, Validators.maxLength(3)]),
      title: new FormControl("", [Validators.required, Validators.maxLength(this.title)]),
      cost: new FormControl("", [Validators.min(1), Validators.max(this.maxCost), Validators.required]),
      file: new FormControl("", [Validators.required]),
    });
  }

  imagePreview(e: any) {
    // @ts-ignore
    const img = (e.target as HTMLInputElement).files[0];
    if (img.type.includes("png") || img.type.includes("jpeg")) {
      this.shop.patchValue({
        file: img
      });
      // @ts-ignore
      this.shop.get('file').updateValueAndValidity()
      if (img) {
        const reader = new FileReader();
        reader.onload = () => {
          this.filePath = reader.result as string;
        }
        reader.readAsDataURL(img)
      }
    }
  }


  hasError(path: string, errorCode: string) {
    return this.shop && this.shop.hasError(errorCode, path);
  }

  submit() {
    // const listing: Product = { ...this.shop.value}
    const a = new FormData();
    const cats = this.shop.value.cats.toString();
    a.append("cost", this.shop.value.cost);
    a.append("title", this.shop.value.title)
    a.append("file", this.shop.value.file)
    a.append("cats", cats)
    this.storeService.upload(a).subscribe(
      next => {
        if (next) {
          this.router.navigate(["store"]).then();
        } else {
          this.showError = true;
        }
      }
    )
  }

  changeSelected($event: any, category: any): void {
    category.selected = $event.selected;
    let list = []
    for (let i = 0; i < this.categories.length; i++) {
      let value = this.categories[i];
      if (value.selected) {
        list.push(value.value)
      }
    }
    this.shop.setControl("cats",
      new FormControl(list, [Validators.required, Validators.maxLength(3)])
    )
  }

}
