import { Component, OnInit } from '@angular/core';
import {Product} from "../model/Product";
import {StoreService} from "../services/store.service";
import {AuthService} from "../services/auth.service";
import {InventoryService} from "../services/inventory.service";
import {Router} from "@angular/router";
import {FormControl, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'app-inventory',
  templateUrl: './inventory.component.html',
  styleUrls: ['./inventory.component.scss']
})
export class InventoryComponent implements OnInit {

  products: Product[] = [];
  product: Product | undefined;
  sellItem: FormGroup;

  constructor(
    private inventoryService: InventoryService,
    public authService: AuthService,
    private storeService: StoreService,
    private router: Router,
  ) { }

  ngOnInit(): void {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['']).then();
    }
    this.authService.getData(false).subscribe(
        next => {
          this.sellItem = new FormGroup({
            amount: new FormControl("",
                [
                  Validators.min(1),
                  Validators.max(next.coins),
                  Validators.required
                ]),
          });
        }
    )

    this.inventoryService.getAll().subscribe(
      next => {
        this.products = next

        hideloader();

        // get images
        for (let i = 0; i < this.products.length; i++) {
          let product = this.products[i];
          this.storeService.getImage(product);
        }
      }
    );
    function hideloader() {
      // @ts-ignore
      document.getElementById('loading').style.display = "none";
    }
  }

  click(product: Product) {
    if (product == this.product) {
      this.product = undefined
      // @ts-ignore
      document.getElementById('stats').className = 'stats'
    } else {
      this.product = product;
      // @ts-ignore
      document.getElementById('stats').classList.add('stats-move')
    }
  }

  sell(id: number) {
    const cats = this.sellItem.value
    console.log(cats)
  }

  hasError(path: string, errorCode: string) {
    return this.sellItem && this.sellItem.hasError(errorCode, path);
  }

  image(array: any) {
    // console.log(array)
    return  'data:image/jpeg;base64,' + array;
  }

}
