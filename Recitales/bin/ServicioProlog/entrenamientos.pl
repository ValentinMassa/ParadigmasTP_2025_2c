% Calcular entrenamientos mínimos necesarios
% Entrada: RolesFaltantes, CapacidadBase, ArtistasDisponibles
% Salida: Entrenamientos (número mínimo de entrenamientos necesarios, -1 si imposible)

entrenamientos_minimos(RolesFaltantes, CapacidadBase, ArtistasDisponibles, Entrenamientos) :-
    (RolesFaltantes =< 0 ->
        Entrenamientos = 0
    ; RolesFaltantes =< CapacidadBase ->
        Entrenamientos = 0
    ;
        RolesRestantes is RolesFaltantes - CapacidadBase,
        Entrenamientos is ceiling(RolesRestantes / 1.0),
        (Entrenamientos > ArtistasDisponibles ->
            Entrenamientos = -1
        ;
            true
        )
    ).

% Predicado que recopila todas las soluciones con findall
resolver_entrenamientos(RolesFaltantes, CapacidadBase, ArtistasDisponibles, Resultado) :-
    findall(E, entrenamientos_minimos(RolesFaltantes, CapacidadBase, ArtistasDisponibles, E), [Resultado]).


