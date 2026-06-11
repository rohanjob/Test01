-- Varaha Nest PostgreSQL Schema Setup

-- Enable UUID extension
create extension if not exists "uuid-ossp";

-- 1. PROFILES Table (linked to auth.users)
create table public.profiles (
    id uuid references auth.users on delete cascade primary key,
    full_name text,
    phone_number text,
    role text default 'USER', -- USER, OWNER, AGENT, BUILDER
    created_at timestamptz default now(),
    updated_at timestamptz default now()
);

alter table public.profiles enable row level security;

-- 2. PROPERTIES Table
create table public.properties (
    id uuid primary key default gen_random_uuid(),
    owner_id uuid references public.profiles(id) on delete cascade not null,
    title text not null,
    description text,
    price numeric not null,
    transaction_type text not null, -- 'BUY', 'RENT', 'COMMERCIAL'
    property_category text not null, -- 'RESIDENTIAL_APARTMENT', 'RESIDENTIAL_HOUSE', 'RENT_PG_ROOM', 'COMMERCIAL_OFFICE', 'COMMERCIAL_SHOP'
    bedrooms integer default 0,
    bathrooms integer default 0,
    balconies integer default 0,
    area_sqft numeric not null,
    address text not null,
    city text not null,
    state text not null,
    latitude double precision,
    longitude double precision,
    furnishing_status text, -- 'FULLY_FURNISHED', 'SEMI_FURNISHED', 'UNFURNISHED'
    parking_spaces integer default 0,
    ownership_type text, -- 'FREEHOLD', 'LEASEHOLD', 'CO_OPERATIVE'
    posted_by text default 'OWNER', -- 'OWNER', 'AGENT', 'BUILDER'
    verified boolean default false,
    status text default 'ACTIVE', -- 'ACTIVE', 'DRAFT', 'ARCHIVED'
    created_at timestamptz default now(),
    updated_at timestamptz default now()
);

alter table public.properties enable row level security;

-- 3. PROPERTY MEDIA Table
create table public.property_media (
    id uuid primary key default gen_random_uuid(),
    property_id uuid references public.properties(id) on delete cascade not null,
    url text not null,
    media_type text default 'IMAGE', -- 'IMAGE', 'VIDEO'
    created_at timestamptz default now()
);

alter table public.property_media enable row level security;

-- 4. FAVORITES Table
create table public.favorites (
    id uuid primary key default gen_random_uuid(),
    user_id uuid references public.profiles(id) on delete cascade not null,
    property_id uuid references public.properties(id) on delete cascade not null,
    created_at timestamptz default now(),
    unique (user_id, property_id)
);

alter table public.favorites enable row level security;

-- 5. LEADS Table (Contact Owner flow)
create table public.leads (
    id uuid primary key default gen_random_uuid(),
    property_id uuid references public.properties(id) on delete cascade not null,
    buyer_id uuid references public.profiles(id) on delete cascade not null,
    message text,
    created_at timestamptz default now()
);

alter table public.leads enable row level security;

-- 6. SUPPORT TICKETS Table
create table public.support_tickets (
    id uuid primary key default gen_random_uuid(),
    user_id uuid references public.profiles(id) on delete cascade not null,
    subject text not null,
    description text not null,
    status text default 'OPEN', -- 'OPEN', 'IN_PROGRESS', 'RESOLVED'
    created_at timestamptz default now()
);

alter table public.support_tickets enable row level security;

-- 7. NOTIFICATIONS Table
create table public.notifications (
    id uuid primary key default gen_random_uuid(),
    user_id uuid references public.profiles(id) on delete cascade not null,
    title text not null,
    body text not null,
    created_at timestamptz default now()
);

alter table public.notifications enable row level security;


----------------- ROW LEVEL SECURITY (RLS) POLICIES -----------------

-- Profiles Policies
create policy "Allow public read access to profiles" on public.profiles
    for select using (true);

create policy "Allow individual user to update their own profile" on public.profiles
    for update using (auth.uid() = id);

create policy "Allow system/user to insert profile on signup" on public.profiles
    for insert with check (auth.uid() = id);

-- Properties Policies
create policy "Allow public read access to active properties" on public.properties
    for select using (status = 'ACTIVE');

create policy "Allow owners to read their own drafts or archived properties" on public.properties
    for select using (auth.uid() = owner_id);

create policy "Allow authenticated users to insert property" on public.properties
    for insert with check (auth.uid() = owner_id);

create policy "Allow owners to update their own properties" on public.properties
    for update using (auth.uid() = owner_id);

create policy "Allow owners to delete their own properties" on public.properties
    for delete using (auth.uid() = owner_id);

-- Property Media Policies
create policy "Allow public read access to property media" on public.property_media
    for select using (true);

create policy "Allow owners to insert media for their properties" on public.property_media
    for insert with check (
        exists (
            select 1 from public.properties
            where properties.id = property_media.property_id
            and properties.owner_id = auth.uid()
        )
    );

create policy "Allow owners to delete media of their properties" on public.property_media
    for delete using (
        exists (
            select 1 from public.properties
            where properties.id = property_media.property_id
            and properties.owner_id = auth.uid()
        )
    );

-- Favorites Policies
create policy "Allow users to read their own favorites" on public.favorites
    for select using (auth.uid() = user_id);

create policy "Allow users to insert their own favorites" on public.favorites
    for insert with check (auth.uid() = user_id);

create policy "Allow users to delete their own favorites" on public.favorites
    for delete using (auth.uid() = user_id);

-- Leads Policies
create policy "Allow user to read their own lead submissions" on public.leads
    for select using (auth.uid() = buyer_id);

create policy "Allow property owners to view leads for their properties" on public.leads
    for select using (
        exists (
            select 1 from public.properties
            where properties.id = leads.property_id
            and properties.owner_id = auth.uid()
        )
    );

create policy "Allow user to insert leads" on public.leads
    for insert with check (auth.uid() = buyer_id);

-- Support Tickets Policies
create policy "Allow user to read their own support tickets" on public.support_tickets
    for select using (auth.uid() = user_id);

create policy "Allow user to insert support tickets" on public.support_tickets
    for insert with check (auth.uid() = user_id);

-- Notifications Policies
create policy "Allow user to read their own notifications" on public.notifications
    for select using (auth.uid() = user_id);


----------------- AUTOMATIC PROFILE CREATION TRIGGER -----------------

-- Automatically create a profile when a new user signs up via auth
create or replace function public.handle_new_user()
returns trigger as $$
begin
  insert into public.profiles (id, full_name, phone_number, role)
  values (
    new.id,
    coalesce(new.raw_user_meta_data->>'full_name', 'User'),
    new.phone,
    'USER'
  );
  return new;
end;
$$ language plpgsql security definer;

create trigger on_auth_user_created
  after insert on auth.users
  for each row execute procedure public.handle_new_user();
