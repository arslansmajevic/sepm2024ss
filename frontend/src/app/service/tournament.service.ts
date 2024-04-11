import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {map, Observable, tap, throwError} from 'rxjs';
import {formatIsoDate} from '../util/date-helper';
import {
  TournamentCreateDto, TournamentDetailDto, TournamentDetailParticipantDto,
  TournamentListDto,
  TournamentSearchParams,
  TournamentStandingsDto, TournamentStandingsTreeDto
} from "../dto/tournament";

const baseUri = environment.backendUrl + '/tournaments';

class ErrorDto {
  constructor(public message: String) {}
}

@Injectable({
  providedIn: 'root'
})
export class TournamentService {
  constructor(
    private http: HttpClient
  ) {
  }

  search(searchParams: TournamentSearchParams): Observable<TournamentListDto[]> {
    if (searchParams.name === '') {
      delete searchParams.name;
    }

    let params = new HttpParams();
    if (searchParams.name) {
      params = params.append('name', searchParams.name);
    }

    if (searchParams.startDate) {
      params = params.append('startDate', formatIsoDate(searchParams.startDate));
    }
    if (searchParams.endDate) {
      params = params.append('endDate', formatIsoDate(searchParams.endDate));
    }

    return this.http.get<TournamentListDto[]>(baseUri, { params })
      .pipe(tap(tournaments => tournaments.map(t => {
        t.startDate = new Date(t.startDate); // Parse date string
        t.endDate = new Date(t.endDate);
      })));
  }

  public create(tournament: TournamentCreateDto): Observable<TournamentDetailDto> {

    return this.http.post<TournamentDetailDto>(
      baseUri,
      tournament
    );

  }

  getStanding(id: number): Observable<TournamentStandingsDto> {
    return this.http.get<TournamentStandingsDto>(`${baseUri}/${id}`);
  }

  generateFirstMatches(id: Number): Observable<TournamentStandingsDto> {
    const url = `${baseUri}/standings/firstRound/${id}`;
    return this.http.put<TournamentStandingsDto>(url, null);
  }

  updateStandings(standings: TournamentStandingsDto, id: Number) : Observable<TournamentStandingsDto> {
    const url = `${baseUri}/${id}`;
    return this.http.put<TournamentStandingsDto>(url, standings);
  }
}
