
%%%%%%%%% Simple Prolog Planner %%%%%%%%%%%%%%%%%%%%%%%%%%
%%%
%%% Based on one of the sample programs in:
%%%
%%% Artificial Intelligence:
%%% Structures and strategies for complex problem solving
%%%
%%% by George F. Luger and William A. Stubblefield
%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%
%%%	by Allysia Freeman
%%% University of Cental Florida
%%% CAP4630 Artificial Intelligence - FALL'16 
%%%	
%%% New States:
%%% * room1 || room2 == the room the arm is in
%%% * inroom1() || inroom2() == the room the block is in
%%% 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
:- module( planner,
	   [
	       plan/4,dls/5,its/5,change_state/3,conditions_met/2,member_state/2,
	       move/3,goDLS/3,goITS/2,go/2,test1/0,test2/0,test3/0
	   ]).

:- [utils].

plan(State, Goal, _, Moves) :-	equal_set(State, Goal),
				write('moves are'), nl,
				reverse_print_stack(Moves).
plan(State, Goal, Been_list, Moves) :-
				move(Name, Preconditions, Actions),
				conditions_met(Preconditions, State),
				change_state(State, Actions, Child_state),
				not(member_state(Child_state, Been_list)),
				stack(Child_state, Been_list, New_been_list),
				stack(Name, Moves, New_moves),
			plan(Child_state, Goal, New_been_list, New_moves),!.

dls(State, Goal, _, Moves, _) :-	
				equal_set(State, Goal),
				write('moves are'), nl,
				reverse_print_stack(Moves).
dls(State, Goal, Been_list, Moves, D) :-
				D > 0,
				move(Name, Preconditions, Actions),
				conditions_met(Preconditions, State),
				change_state(State, Actions, Child_state),
				not(member_state(Child_state, Been_list)),
				stack(Child_state, Been_list, New_been_list),
				stack(Name, Moves, New_moves),
				D1 is D - 1,
			dls(Child_state, Goal, New_been_list, New_moves, D1), !.

its(State, Goal, Been_list, Moves, D) :-
				D1 is D + 1,
				dls(State, Goal, Been_list, Moves, D1), !.

its(State, Goal, Been_list, Moves, D) :-
				D1 is D + 1,
			its(State, Goal, Been_list, Moves, D1), !.

change_state(S, [], S).
change_state(S, [add(P)|T], S_new) :-	change_state(S, T, S2),
					add_to_set(P, S2, S_new), !.
change_state(S, [del(P)|T], S_new) :-	change_state(S, T, S2),
					remove_from_set(P, S2, S_new), !.
conditions_met(P, S) :- subset(P, S).

member_state(S, [H|_]) :-	equal_set(S, H).
member_state(S, [_|T]) :-	member_state(S, T).

/* move types */

move(goroom1, [room2, holding(X)],
		[del(room2), del(inroom2(X)),
			add(room1), add(inroom1(X) )]).

move(goroom1, [room2, handempty],
		[del(room2),
			add(room1)] ).

move(goroom2, [room1, holding(X)],
		[del(room1), del(inroom1(X)),
				add(room2), add(inroom2(X))]).

move(goroom2, [room1, handempty],
		[del(room1),
				add(room2)]).

move(pickup(X), [room1, handempty, clear(X), on(X, Y), inroom1(X), inroom1(Y)],
		[del(handempty), del(clear(X)), del(on(X, Y)),
				 add(clear(Y)),	add(holding(X))]).

move(pickup(X), [room1, handempty, clear(X), ontable(X), inroom1(X)],
		[del(handempty), del(clear(X)), del(ontable(X)),
				 add(holding(X))]).

move(pickup(X), [room2, handempty, clear(X), on(X, Y), inroom2(X), inroom2(Y)],
		[del(handempty), del(clear(X)), del(on(X, Y)),
				 add(clear(Y)),	add(holding(X))]).

move(pickup(X), [room2, handempty, clear(X), ontable(X), inroom2(X)],
		[del(handempty), del(clear(X)), del(ontable(X)),
				 add(holding(X))]).

move(stack(X, Y), [room1, holding(X), clear(Y), inroom1(Y)],
		[del(holding(X)), del(clear(Y)), add(handempty), add(on(X, Y)),
				  add(clear(X))]).

move(stack(X, Y), [room2, holding(X), clear(Y), inroom2(Y)],
		[del(holding(X)), del(clear(Y)), add(handempty), add(on(X, Y)),
				  add(clear(X))]).

move(putdown(X), [room1, holding(X)],
		[del(holding(X)), add(ontable(X)), add(clear(X)),
				  add(handempty)]).

move(putdown(X), [room2, holding(X)],
		[del(holding(X)), add(ontable(X)), add(clear(X)),
				  add(handempty)]).

%% run commands

%%go(S, G)     :- plan(S, G, [S], []).
go(S,G)      :- goITS(S,G).
goDLS(S,G,D) :-  dls(S, G, [S], [], D).
goITS(S,G)   :-  its(S, G, [S], [], 1).

%% given in assignment specs, test 2 rooms 
/* TEST SPACE:

	  |A|						|A|
	  |B|						|B|
-----------------	->	------------------
/				\       /				 \
	room1					  room2
*/
test1 :- go([handempty, ontable(b), on(a,b), clear(a), inroom1(b), inroom1(a), room1],
			  [handempty, ontable(b), on(a,b), clear(a), inroom2(b), inroom2(a), room1]).

%% test one room 
/* TEST SPACE:

|D|                                   |C|
|A|   |B|    |C|		|B|    |D|    |A|
-----------------	->	------------------
/				\       /				 \
*/
test2 :- go([handempty, ontable(a), on(d,a), clear(d), ontable(b), clear(b), ontable(c), clear(c), inroom1(a), inroom1(b), inroom1(c), inroom1(d), room1],
	          [handempty, ontable(b), clear(b), ontable(d), clear(d), ontable(a), on(c,a), clear(c), inroom1(a), inroom1(b), inroom1(c), inroom1(d), room1]).

%% test two rooms
/* TEST SPACE:
	
							|C|							   |B|					 
	|A|						|B|							   |C|					 |A|
-----------------	&	------------------   ->   	-----------------	&	------------------
/				\       /				 \          /				\       /				 \
	room1 					  room2                 	room1 					  room2
*/
test3 :- go([handempty, ontable(a), clear(a), ontable(b), on(c,b), clear(c), inroom1(a), inroom2(b), inroom2(c), room1],
	          [handempty, ontable(c), on(b,c), clear(b), ontable(a), clear(a), inroom2(a), inroom1(b), inroom1(c), room1]).