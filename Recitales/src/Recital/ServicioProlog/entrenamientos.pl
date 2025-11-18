% Calcular entrenamientos mínimos necesarios
entrenamientos_minimos(RolesFaltantes, CapacidadBase, ArtistasDisponibles, Entrenamientos) :-
    RolesRestantes is RolesFaltantes - CapacidadBase,
    (RolesRestantes =< 0 ->
        Entrenamientos = 0
    ;
        Entrenamientos is ceiling(RolesRestantes / 1.0),
        (Entrenamientos > ArtistasDisponibles ->
            Entrenamientos = -1
        ;
            true
        )
    ).

