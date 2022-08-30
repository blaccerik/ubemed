import {User} from "./User";
import {CasinoWheelPlayer} from "./CasinoWheelPlayer";

export class WheelEnterBroadcast {
    mid: number;
    value: number;
    players: CasinoWheelPlayer[]
    user: User;
    isNormalBroadcast: boolean
}