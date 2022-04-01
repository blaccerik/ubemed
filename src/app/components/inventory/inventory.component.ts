import { Component, OnInit } from '@angular/core';
import {Product} from "../model/Product";
import {StoreService} from "../services/store.service";
import {AuthService} from "../services/auth.service";
import {InventoryService} from "../services/inventory.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-inventory',
  templateUrl: './inventory.component.html',
  styleUrls: ['./inventory.component.scss']
})
export class InventoryComponent implements OnInit {

  products: Product[] = [];

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

    this.inventoryService.getAll().subscribe(
      next => {
        console.log(next)
        this.products = next

        // get images
        for (let i = 0; i < this.products.length; i++) {
          let product = this.products[i];
          const value = localStorage.getItem("image-" + product.id)

          // save to localstorage
          if (!value) {
            this.storeService.getImg(product.id).subscribe(
              (next: any) => {
                product.file = next.file;
                localStorage.setItem("image-" + product.id, product.file);
              }
            )
          } else {
            product.file = value;
          }
        }
      }
    );
    function hideloader() {
      // @ts-ignore
      document.getElementById('loading').style.display = "none";
    }
  }

  image(array: any) {
    // console.log(array)
    return  'data:image/jpeg;base64,' + array;
  }

}
